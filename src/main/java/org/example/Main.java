/*
Vježba 16:
Kreirati novi Java projekt koji će sadržavati perzistentnu klasu „Account” s varijablama „id” (Long),
„username” (String) i „balance” (BigDecimal) te odgovarajuću konfiguraciju u datoteci „hibernate.cfg.xml”.
Kreirati novu klasu „HibernateUtil” koja će sadržavati privatnu metodu „buildSessionFactory” te javne
metode „getSessionFactory” koja će vraćati kreirani „sessionFactory” objekt te metodu „shutdown” koja će zatvarati objekt „sessionFactory”.

Kreirati klasu koja će sadržavati „main” metodu koja će imati metode za kreiranje objekta klase „Account”
korištenjem sesije i transakcije, metodu za provođenje jedne transakcije koja prenosi sredstva s jednog računa na drugi te obavlja
„commit” ako ima dovoljno sredstava na bankovnom računa, odnosno „rollback” ako nema dovoljno sredstava.

Na kraju napisati metodu za provjeru stanja zadanog računa koja također koristi Hibernate sesiju.

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

Long acc1 = createAccount("Otp", new BigDecimal("15000"));
Long acc2 = createAccount("Josip", new BigDecimal("2340"));
Long acc3 = createAccount("Biznis", new BigDecimal("79487"));



    }


    public static Long createAccount(String username, BigDecimal initialBalance) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Account account = new Account(username, initialBalance);
            Long accountId = (Long) session.persist(account);
            transaction.commit();
            System.out.println("Account created with ID: " + accountId);
            return accountId;
        }
    }

    // METODA 2: Transfer novca između računa
    public static boolean transferFunds(String fromUser, String toUser, BigDecimal amount) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Account receiverAccount = (Account) session.createQuery("FROM Account WHERE username = :username")
                    .setParameter("username", receiverUsername)
                    .uniqueResult();
                    .createQuery("FROM Account WHERE username = :username")
                    .setParameter("username", fromUser)
                    .uniqueResult();



            System.out.println("Pošiljatelj (" + sender.getUsername() + "): " + sender.getBalance());
            System.out.println("Primatelj (" + receiver.getUsername() + "): " + receiver.getBalance());
            System.out.println("Iznos za transfer: " + amount);

            // Provjeri ima li dovoljno novca
            if (sender.getBalance().compareTo(amount) >= 0) {
                // Ima dovoljno - izvrši transfer
                sender.setBalance(sender.getBalance().subtract(amount));
                receiver.setBalance(receiver.getBalance().add(amount));

                // Spremi promjene
                session.update(sender);
                session.update(receiver);

                transaction.commit();
                System.out.println("Transfer uspješan!");
                System.out.println("Novo stanje " + sender.getUsername() + ": " + sender.getBalance());
                System.out.println("Novo stanje " + receiver.getUsername() + ": " + receiver.getBalance());
                return true;

            } else {
                // Nema dovoljno novca - rollback
                System.out.println("Nedovoljno sredstava na računu!");
                System.out.println("Trenutno: " + sender.getBalance() + ", potrebno: " + amount);
                transaction.rollback();
                return false;
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Greška pri transferu: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    // METODA 3: Provjera stanja računa
    public static void checkBalance(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Account account = (Account) session
                    .createQuery("FROM Account WHERE username = :username")
                    .setParameter("username", username)
                    .uniqueResult();

            if (account != null) {
                System.out.println(username + ": " + account.getBalance() + " kn");
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

