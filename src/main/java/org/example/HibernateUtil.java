/*Kreirati novu klasu „HibernateUtil” koja će sadržavati privatnu metodu „buildSessionFactory” te javne
        metode „getSessionFactory” koja će vraćati kreirani „sessionFactory” objekt te metodu „shutdown” koja će zatvarati objekt „sessionFactory”.*/

package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Kreiranje SessionFactory iz hibernate.cfg.xml konfiguracijske datoteke
            return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            // U slučaju greške, ispisujemo poruku i bacamo iznimku
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }


    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("SessionFactory closed.");
        }
    }
}





