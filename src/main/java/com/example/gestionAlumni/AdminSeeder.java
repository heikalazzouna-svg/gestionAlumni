package com.example.gestionAlumni;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Lightweight seeder that ensures admin, alumni, and student test accounts always exist.
 * Uses only native SQL to avoid Hibernate mapping issues with null boolean columns.
 */
@Component
@org.springframework.context.annotation.Profile("!test")
public class AdminSeeder implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public AdminSeeder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            // 1. Fix legacy DTYPE values
            entityManager.createNativeQuery(
                "UPDATE app_user SET dtype='admin' WHERE dtype='Administrator'"
            ).executeUpdate();

            // 2. Fix NULL boolean columns that crash Hibernate mapping
            entityManager.createNativeQuery(
                "UPDATE app_user SET verified = false WHERE verified IS NULL"
            ).executeUpdate();
            entityManager.createNativeQuery(
                "UPDATE app_user SET active = true WHERE active IS NULL"
            ).executeUpdate();

            String encodedPassword = passwordEncoder.encode("password123");

            // 3. Admin Account (test@admin.com)
            Number countAdmin = (Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM app_user WHERE email = 'test@admin.com'"
            ).getSingleResult();

            if (countAdmin.intValue() > 0) {
                entityManager.createNativeQuery(
                    "UPDATE app_user SET password = :pwd, active = true, verified = true, dtype = 'admin' WHERE email = 'test@admin.com'"
                ).setParameter("pwd", encodedPassword).executeUpdate();
                System.out.println("✅ ADMIN PASSWORD RESET (test@admin.com / password123)");
            } else {
                entityManager.createNativeQuery(
                    "INSERT INTO app_user (dtype, first_name, last_name, email, password, active, verified, graduation_year) " +
                    "VALUES ('admin', 'Super', 'Admin', 'test@admin.com', :pwd, true, true, 0)"
                ).setParameter("pwd", encodedPassword).executeUpdate();
                System.out.println("✅ DEFAULT ADMIN CREATED (test@admin.com / password123)");
            }

            // 4. Alumni Account (test@alumni.com)
            Number countAlumni = (Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM app_user WHERE email = 'test@alumni.com'"
            ).getSingleResult();

            if (countAlumni.intValue() > 0) {
                entityManager.createNativeQuery(
                    "UPDATE app_user SET password = :pwd, active = true, verified = true, dtype = 'alumni' WHERE email = 'test@alumni.com'"
                ).setParameter("pwd", encodedPassword).executeUpdate();
                System.out.println("✅ ALUMNI ACCOUNT RESET (test@alumni.com / password123)");
            } else {
                entityManager.createNativeQuery(
                    "INSERT INTO app_user (dtype, first_name, last_name, email, password, active, verified, graduation_year, current_company, current_job, open_to_mentoring) " +
                    "VALUES ('alumni', 'Mohamed', 'Alumni', 'test@alumni.com', :pwd, true, true, 2022, 'Google', 'Software Engineer', true)"
                ).setParameter("pwd", encodedPassword).executeUpdate();
                System.out.println("✅ DEFAULT ALUMNI CREATED (test@alumni.com / password123)");
            }

            // 5. Student Account (test@student.com)
            Number countStudent = (Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM app_user WHERE email = 'test@student.com'"
            ).getSingleResult();

            if (countStudent.intValue() > 0) {
                entityManager.createNativeQuery(
                    "UPDATE app_user SET password = :pwd, active = true, verified = true, dtype = 'student' WHERE email = 'test@student.com'"
                ).setParameter("pwd", encodedPassword).executeUpdate();
                System.out.println("✅ STUDENT ACCOUNT RESET (test@student.com / password123)");
            } else {
                entityManager.createNativeQuery(
                    "INSERT INTO app_user (dtype, first_name, last_name, email, password, active, verified, predicted_grad_year) " +
                    "VALUES ('student', 'Sarra', 'Student', 'test@student.com', :pwd, true, true, 2025)"
                ).setParameter("pwd", encodedPassword).executeUpdate();
                System.out.println("✅ DEFAULT STUDENT CREATED (test@student.com / password123)");
            }

        } catch (Exception e) {
            System.err.println("⚠️ AdminSeeder error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
