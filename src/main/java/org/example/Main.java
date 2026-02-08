/*
Vježba 16:
Kreirati novi Java projekt koji će sadržavati perzistentnu klasu „Account” s varijablama „id” (Long),
„username” (String) i „balance” (BigDecimal) te odgovarajuću konfiguraciju u datoteci „hibernate.cfg.xml”. ✓

Kreirati novu klasu „HibernateUtil” koja će sadržavati privatnu metodu „buildSessionFactory” te javne
metode „getSessionFactory” koja će vraćati kreirani „sessionFactory” objekt te metodu „shutdown” koja će zatvarati objekt „sessionFactory”. ✓

Kreirati klasu koja će sadržavati „main” metodu koja će imati metode za kreiranje objekta klase „Account”
korištenjem sesije i transakcije, metodu za provođenje jedne transakcije koja prenosi sredstva s jednog računa na drugi te obavlja
„commit” ako ima dovoljno sredstava na bankovnom računa, odnosno „rollback” ako nema dovoljno sredstava.✓

Na kraju napisati metodu za provjeru stanja zadanog računa koja također koristi Hibernate sesiju.✓

Primjer implementacije metode za kreiranje računa je prikazan u nastavku:
public static Long createAccount(String username, BigDecimal initialBalance) {
  try (Session session = HibernateUtil.getSessionFactory().openSession()) {
    Transaction transaction = session.beginTransaction();
    Account account = new Account(username, initialBalance);
    Long accountId = (Long) session.save(account);
    transaction.commit()
     System.out.println("Account created with ID: " + accountId);
    return accountId;
  }
}


izgled upita za dohvaćanje korisnika iz tablice:
Account receiverAccount = (Account) session.createQuery("FROM Account WHERE username = :username")
    .setParameter("username", receiverUsername)
    .uniqueResult();
*/

package org.example;


import org.example.model.Account;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        try {
        Long acc1 = createAccount("Otp", new BigDecimal("15000"));
        Long acc2 = createAccount("Josip", new BigDecimal("2340"));
        Long acc3 = createAccount("Biznis", new BigDecimal("79487"));

        chkBalance("Otp");
        chkBalance("Josip");
        chkBalance("Biznis");

        transferFunds("Otp", "Josip", BigDecimal.valueOf(14000));
        transferFunds("Biznis", "Josip", BigDecimal.valueOf(80000));


        chkBalance("Josip");

    } finally {
            HibernateUtil.shutdown();
        }
        }


    public static Long createAccount(String username, BigDecimal initialBalance) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Account account = new Account(username, initialBalance);
            session.persist(account);
            Long accountId = account.getId();
            transaction.commit();
            System.out.println("Account created with ID: " + accountId);
            return accountId;
        }
    }


    public static boolean transferFunds(String senderUsername, String receiverUsername, BigDecimal amount) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();


            Account senderAccount = (Account) session.createQuery("FROM Account WHERE username = :username")
                    .setParameter("username", senderUsername)
                    .uniqueResult();


            Account receiverAccount = (Account) session.createQuery("FROM Account WHERE username = :username")
                    .setParameter("username", receiverUsername)
                    .uniqueResult();

            if (senderAccount == null || receiverAccount == null) {
                System.out.println("Račun nije pronađen!");
                if (transaction != null) transaction.rollback();
                return false;
            }

            System.out.println("Pošiljatelj: " + senderAccount.getUsername() + ", Stanje: " + senderAccount.getBalance());
            System.out.println("Primatelj: " + receiverAccount.getUsername() + ", Stanje: " + receiverAccount.getBalance());
            System.out.println("Iznos za transfer: " + amount);
            if (senderAccount.getBalance().compareTo(amount) >= 0) {

                senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
                receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

                session.merge(senderAccount);
                session.merge(receiverAccount);

                transaction.commit();
                System.out.println("Transfer uspješan!");
                System.out.println("Nakon transfera:");
                System.out.println("Pošiljatelj: " + senderAccount.getUsername() + ", Stanje: " + senderAccount.getBalance());
                System.out.println("Primatelj: " + receiverAccount.getUsername() + ", Stanje: " + receiverAccount.getBalance());
                return true;
            } else {
                System.out.println(senderAccount.getUsername() +" nema dovoljno sredstava na računu!");
                transaction.rollback();
                return false;
            }

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Greška pri transferu: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }


            public static void chkBalance (String username){
                Session session = HibernateUtil.getSessionFactory().openSession();

                try {
                    Account account = (Account) session
                            .createQuery("FROM Account WHERE username = :username")
                            .setParameter("username", username)
                            .uniqueResult();

                    if (account != null) {
                        System.out.println(username + ": " + account.getBalance() + " eur");
                    } else {
                        System.out.println(username + ": Račun nije pronađen");
                    }

                } catch (Exception e) {
                    System.out.println("Greška pri provjeri stanja: " + e.getMessage());
                } finally {
                    session.close();
                }
            }

        }



