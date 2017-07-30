package com.spring.ioc;

import com.spring.AppContext;
import com.spring.ioc.annotation.Transactional;
import com.spring.jdbc.DBUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class TransactionProxy implements InvocationHandler {
    private Object target;

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Class<?> targetClass = target.getClass();
        Method methodImpl =  targetClass.getMethod(method.getName(), method.getParameterTypes());
        if (target.getClass().isAnnotationPresent(Transactional.class)
                || methodImpl.isAnnotationPresent(Transactional.class)) {
            Object result = null;
            Connection conn = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
            if (conn == null) {
                conn = DBUtil.getConnection();
                AppContext.getContext().setObject(AppContext.CONNECTION, conn);
            }
            conn.setAutoCommit(false);
            try {
                result = method.invoke(target, args);
                conn.commit();
            } catch (Throwable e) {
                conn.rollback();
                System.out.println("事务回滚");
                throw e;
            } finally {
                if (!conn.isClosed()) {
                    DBUtil.close(conn, null, null);
                    AppContext.getContext().removeObject(AppContext.CONNECTION);
                }
            }

            return result;
        } else {
            return method.invoke(target, args);
        }

    }

}
