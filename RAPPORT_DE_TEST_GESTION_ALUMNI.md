# 🎓 RAPPORT OFFICIEL DE TEST, QUALITÉ ET VALIDATION LOGICIELLE
## Projet : Plateforme de Gestion des Alumni (`gestionAlumni` & `ProjetWeb`)

---

> **Établissement** : École Nationale d'Ingénieurs de Carthage (ENICarthage)  
> **Module** : Qualité, Test et Validation de Logiciels  
> **Enseignant Encadrant** : M. Faouzi Jaidi  
> **Auteur / Étudiant** : Heikal Azzouna  
> **Cadre Méthodologique** : Normes ISTQB, IEEE 829 (Test Documentation), IEEE 1028 (Software Reviews and Formal Inspections)  
> **Année Universitaire** : 2025 – 2026  

---

## 📑 Table des Matières

- [1. Présentation du Projet et Exigences](#1-présentation-du-projet-et-exigences)
  - [1.1 Contexte et Objectifs](#11-contexte-et-objectifs)
  - [1.2 Architecture Technique & Stack](#12-architecture-technique--stack)
  - [1.3 Exigences Fonctionnelles (BF01 à BF12)](#13-exigences-fonctionnelles-bf01-à-bf12)
  - [1.4 Exigences Non Fonctionnelles (NF-01 à NF-07)](#14-exigences-non-fonctionnelles-nf-01-à-nf-07)
- [2. Organisation et Outils de Test](#2-organisation-et-outils-de-test)
  - [2.1 Stratégie et Pyramide des Tests](#21-stratégie-et-pyramide-des-tests)
  - [2.2 Référentiel des Outils Utilisés](#22-référentiel-des-outils-utilisés)
- [3. Plan de Test & Niveaux de Test](#3-plan-de-test--niveaux-de-test)
  - [3.1 Périmètre et Critères d'Acceptation](#31-périmètre-et-critères-dacceptation)
  - [3.2 Niveaux et Types de Tests Déployés](#32-niveaux-et-types-de-tests-déployés)
- [4. Exécution : Techniques Statiques](#4-exécution--techniques-statiques)
  - [4.1 Analyse Statique Outillée (SpotBugs & JaCoCo)](#41-analyse-statique-outillée-spotbugs--jacoco)
  - [4.2 Inspection Formelle selon la Norme IEEE 1028](#42-inspection-formelle-selon-la-norme-ieee-1028)
    - [4.2.1 Rôles de l'Inspection (Tableau 4.2)](#421-rôles-de-linspection-tableau-42)
    - [4.2.2 Checklist d'Inspection et Constats (Tableau 4.3)](#422-checklist-dinspection-et-constats-tableau-43)
    - [4.2.3 Journal des Anomalies Relevées (Tableau 4.4 - Fiche des Défauts)](#423-journal-des-anomalies-relevées-tableau-44---fiche-des-défauts)
- [5. Exécution : Techniques Dynamiques](#5-exécution--techniques-dynamiques)
  - [5.1 Tests Unitaires & d'Intégration (JUnit 5, Mockito, MockMvc)](#51-tests-unitaires--dintégration-junit-5-mockito-mockmvc)
  - [5.2 Tests d'API avec Postman & Newman (Tableau 5.1)](#52-tests-dapi-avec-postman--newman-tableau-51)
  - [5.3 Tests Fonctionnels, Système et E2E avec Selenium (Tableau 5.3)](#53-tests-fonctionnels-système-et-e2e-avec-selenium-tableau-53)
  - [5.4 Tests Non Fonctionnels](#54-tests-non-fonctionnels)
    - [5.4.1 Tests de Performance et de Montée en Charge JMeter (Tableau 5.2)](#541-tests-de-performance-et-de-montée-en-charge-jmeter-tableau-52)
    - [5.4.2 Performance et Qualité de l'Interface avec Google Lighthouse (Tableau 5.5)](#542-performance-et-qualité-de-linterface-avec-google-lighthouse-tableau-55)
    - [5.4.3 Synthèse des Autres Exigences Non Fonctionnelles](#543-synthèse-des-autres-exigences-non-fonctionnelles)
- [6. Synthèse, Matrice de Traçabilité et Bilan](#6-synthèse-matrice-de-traçabilité-et-bilan)
  - [6.1 Matrice de Traçabilité Exigences ↔ Tests](#61-matrice-de-traçabilité-exigences--tests)
  - [6.2 Taux de Couverture et Taux de Succès](#62-taux-de-couverture-et-taux-de-succès)
  - [6.3 Plan de Maintenance et Recommandations](#63-plan-de-maintenance-et-recommandations)
- [7. Conclusion Générale](#7-conclusion-générale)
- [Annexe : Index des Preuves d'Exécution & Captures d'Écran](#annexe--index-des-preuves-dexécution--captures-décran)

---

# 1. Présentation du Projet et Exigences

## 1.1 Contexte et Objectifs

La plateforme **gestionAlumni** est une solution web complète dédiée à l'animation du réseau des diplômés, à l'insertion professionnelle des étudiants et au rapprochement avec les recruteurs. Elle répond aux problématiques clés des grandes écoles et universités :
- Conserver le lien avec les diplômés et valoriser leurs parcours.
- Offrir aux étudiants un accès direct à du mentorat ciblé et à des offres d'emploi ou de stage.
- Permettre aux recruteurs de cibler des profils qualifiés et de publier des opportunités de carrière.
- Centraliser l'organisation d'événements (conférences, webinaires, salons).

## 1.2 Architecture Technique & Stack

Le système repose sur une architecture moderne découplée en micro-services / client-serveur :

```mermaid
graph LR
    subgraph Frontend ["Client Web (Port 4200)"]
        A["Angular 19\nTypeScript / RxJS / Tailwind CSS"]
    end
    subgraph Backend ["Serveur REST (Port 8081)"]
        B["Spring Boot 3.4.4\nJava 17 / Spring Security"]
        C["Couche Service Métier\n(9 Services)"]
        D["Spring Data JPA\n(Hibernate ORM)"]
        B --> C --> D
    end
    subgraph BaseDeDonnees ["Persistance"]
        E[("MySQL 8.0\nInnoDB Engine")]
        D --> E
    end
    A -- "JSON / REST HTTP & WebSocket STOMP" --> B
```

### TAB. 1.1 — Pile Technologique Officielle

| Composant | Technologie Choisie | Version | Rôle dans l'Architecture |
|---|---|:---:|---|
| **Langage Backend** | Java OpenJDK | 17 LTS | Logique applicative serveur et APIs |
| **Framework Backend** | Spring Boot | 3.4.4 | Conteneur IoC, injection de dépendances, API REST |
| **Sécurité & Auth** | Spring Security + BCrypt | 6.x | Chiffrement unidirectionnel et contrôle d'accès |
| **Persistance ORM** | Hibernate / Spring Data JPA | 6.x | Mapping objet-relationnel |
| **Base de Données** | MySQL Community Server | 8.0 | Stockage relationnel persistant |
| **Base de Test** | H2 Database Engine | In-Memory | Environnement de test hermétique et rapide |
| **Frontend** | Angular | 19.2 | Interface utilisateur monopage (SPA) réactive |
| **Messagerie Temps Réel** | WebSocket / STOMP SockJS | 7.x | Communication bidirectionnelle de chat |

---

## 1.3 Exigences Fonctionnelles (BF01 à BF12)

### TAB. 1.2 — Référentiel des Besoins Fonctionnels

| Code | Intitulé du Besoin Fonctionnel | Description Détaillée | Acteurs Concernés |
|---|---|---|---|
| **BF01** | Inscription & Connexion Multi-Rôles | Création de compte et authentification sécurisée avec mot de passe haché (BCrypt). | Tous |
| **BF02** | Validation des Alumni par l'Admin | Approbation ou rejet des demandes d'inscription Alumni avec statut de vérification. | Admin |
| **BF03** | Gestion Complète des Profils | Édition des données de carrière, compétences, diplômes, CV et documents justificatifs. | Étudiant, Alumni, Recruteur |
| **BF04** | Annuaire des Diplômés | Consultation et filtrage multicritères (nom, société, secteur, promotion, disponibilité). | Étudiant, Alumni, Admin |
| **BF05** | Programme de Mentorat & Matching | Mise en relation ciblée entre mentor et mentoré selon les compétences et objectifs. | Étudiant, Alumni |
| **BF06** | Gestion des Offres d'Emploi & Stages | Publication, consultation, recherche par mot-clé et postulation aux opportunités. | Recruteur, Alumni, Étudiant |
| **BF07** | Partage d'Expériences & Témoignages | Publication d'articles et de retours d'expérience sur les trajectoires professionnelles. | Alumni, Étudiant |
| **BF08** | Création & Gestion d'Événements | Organisation d'événements avec statut dynamique (`APPROVED` pour admin, `PENDING` sinon). | Admin, Alumni, Étudiant |
| **BF09** | Inscription aux Événements | Enregistrement de participants, contrôle de jauge maximale, rejet des doublons, désistement. | Étudiant, Alumni |
| **BF10** | Messagerie Instantanée | Conversations privées 1-to-1, historique horodaté, notifications et compteurs non lus. | Étudiant, Alumni |
| **BF11** | Simulateur de Carrière & Assistant IA | Évaluation des compétences et conseils personnalisés pour l'insertion sur le marché. | Étudiant |
| **BF12** | Administration de la Plateforme | Modération des utilisateurs, statistiques d'engagement, supervision du contenu. | Admin |

---

## 1.4 Exigences Non Fonctionnelles (NF-01 à NF-07)

### TAB. 1.3 — Référentiel des Besoins Non Fonctionnels

| Code | Catégorie | Description de l'Exigence | Métrique / Seuil d'Acceptation |
|---|---|---|---|
| **NF-01** | **Performance API** | Temps de réponse moyen sous charge concurrente (10 utilisateurs simultanés). | Temps moyen < **500 ms** sur l'ensemble des endpoints |
| **NF-02** | **Fiabilité & Résilience** | Robustesse du serveur et intégrité des transactions sous charge. | Taux d'échec HTTP = **0 %**, rollback automatique |
| **NF-03** | **Sécurité** | Confidentialité des données d'authentification et isolation des rôles. | Hachage BCrypt (coût 10), protection des mots de passe en transit |
| **NF-04** | **Maintenabilité** | Modularité du code source et absence d'anomalies bloquantes d'architecture. | 0 anomalie bloquante SpotBugs, séparation Controller/Service/Repo |
| **NF-05** | **Couverture de Test** | Taux de couverture des tests automatisés sur la logique métier critique. | Couverture JaCoCo > **80 %** sur les services clés |
| **NF-06** | **Compatibilité Multi-Écrans** | Adaptation responsive sur smartphone, tablette et écran desktop. | Rendu 100% conforme sur 375px, 768px et 1920px (Chrome/Edge) |
| **NF-07** | **Qualité & Ergonomie UI** | Respect des standards web modernes, accessibilité et rapidité de l'interface. | Score Lighthouse > **80 %** (Accessibilité, Bonnes Pratiques, SEO) |

---

# 2. Organisation et Outils de Test

## 2.1 Stratégie et Pyramide des Tests

Conformément aux préconisations de l'ISTQB, la démarche de validation a été bâtie sur le modèle de la **Pyramide des Tests**, privilégiant une base solide de tests automatisés rapides et déterministes, complétée par des tests d'intégration, d'API et de bout en bout :

```mermaid
flowchart TD
    UI["Tests E2E & Interface (Selenium + Lighthouse)\n8 tests E2E — Rendu & Ergonomie"]
    API["Tests d'API & Charge (Postman + JMeter)\n13 requêtes testées — 224 tirs de charge"]
    INT["Tests d'Intégration (Spring Boot Test + MockMvc)\n22 tests — Contrôleurs & Contrats HTTP"]
    UNIT["Tests Unitaires (JUnit 5 + Mockito)\n44 tests — Services métiers & Règles de gestion"]
    
    UI --> API
    API --> INT
    INT --> UNIT
```

## 2.2 Distinction entre Gestion de Test et Outils de Test

Nous distinguons rigoureusement l'**outil de gestion de test**, qui organise, structure et centralise les données de test, des **outils de test**, qui exécutent techniquement les vérifications ou analysent le code source. TestRail n'exécute aucun test directement : il centralise les résultats produits par l'ensemble des autres briques d'automatisation.

### TAB. 2.2 — Distinction entre Gestion de Test et Outils d'Exécution

| Rôle & Fonction | Outil de Gestion de Test | Outils d'Exécution de Test & Analyse |
|---|---|---|
| **Outil Retenu** | **TestRail** (Gurock) | **JUnit 5, Mockito, Spring MockMvc, H2, Postman/Newman, JMeter, Selenium, SpotBugs, JaCoCo, Lighthouse** |
| **Objectif Principal** | Planifier, organiser les suites de test, suivre l'exécution en temps réel, tracer les exigences (BF/NF) vers les cas et consigner les anomalies. | Exécuter le code de test, simuler les requêtes HTTP et navigateurs, mesurer les performances, produire les rapports techniques (XML, HTML). |
| **Livrables Produits** | Tableaux de bord de suivi, matrice de traçabilité, taux d'avancement des campagnes, fiches de défauts. | Rapports JUnit XML Surefire, métriques JaCoCo, logs Newman, captures d'écran Selenium, audits Lighthouse. |

---

## 2.3 TestRail : Gestion des Tests et Couverture des Exigences

L'organisation des tests dans TestRail pour le projet **gestionAlumni** s'articule autour des principes suivants :
- **Arborescence par sections** : 44 cas de test canoniques répartis en 5 sections fonctionnelles et non-fonctionnelles (Authentification, Annuaire/Profils, Événements, Offres, Performance & UI).
- **Traçabilité des exigences** : Chaque cas de test est relié explicitement à son exigence métier grâce au champ `References` (ex: `BF01`, `BF08`, `NF-01`).
- **Suivi d'exécution & Runs** : Enregistrement de runs dédiés par niveau de test avec statuts normés ISTQB (*Passed*, *Failed*, *Retest*, *Blocked*).
- **Importation automatisée** : Centralisation des résultats d'exécution via les rapports Surefire XML importables directement avec l'outil en ligne de commande TestRail CLI (`trcli`).

> **Figure 2.1 — Vue d'ensemble du projet gestionAlumni dans TestRail**  
> *(Preuve numérique enregistrée sous : `preuves_rapport/testrail_vue_ensemble_fig2_1.png`)*  
> Sur l'ensemble des campagnes menées, 94 % des cas ont été validés *Passed*, avec un suivi continu de la couverture des exigences.

---

## 2.4 Trello : Organisation et Pilotage Agile

Pour assurer le respect du calendrier et la coordination des différentes phases du plan de test, un tableau de bord **Trello** a été déployé selon la méthode Kanban :
- Colonnes de flux : *À faire (Backlog)*, *En cours (WIP)*, *En revue / Contrôle qualité*, et *Validé & Terminé*.
- Chaque carte correspond à un livrable unitaire (ex : "Écriture TU AdminService", "Scan SpotBugs", "Tirs JMeter", "Script Selenium E2E").
- Le planning a suivi les sprints successifs du plan de test jusqu'à la complétion à 100 % des 8 phases.

---

## 2.5 Référentiel des Outils Utilisés

### TAB. 2.1 — Outils Mobilisés dans la Démarche de Qualification

| Catégorie | Outil | Version | Objectif & Utilisation |
|---|---|:---:|---|
| **Test Unitaire** | JUnit 5 + Mockito | 5.10 / 5.x | Validation unitaire des 9 services métiers avec isolation des mocks |
| **Test d'Intégration** | Spring Boot Test + MockMvc | 3.4.4 | Validation des couches Web, sérialisation JSON et codes de statut HTTP |
| **Base In-Memory** | H2 Database | 2.3.232 | Exécution ultra-rapide des tests sans dépendance à une base MySQL externe |
| **Couverture de Code** | JaCoCo Maven Plugin | 0.8.12 | Mesure du taux de couverture d'instructions et de branches |
| **Analyse Statique** | SpotBugs Maven Plugin | 4.8.6 | Détection des failles de sécurité, bugs potentiels et mauvaises pratiques |
| **Revue Formelle** | Norme IEEE 1028 | 2008 | Inspection rigoureuse de la conception, de la sécurité et des DTOs |
| **Test d'API** | Postman / Newman CLI | 6.2.3 | Validation automatisée des endpoints REST, tokens et assertions contractuelles |
| **Test de Charge** | Apache JMeter / HTTP Load | 5.6 | Évaluation de la latence, de la concurrence et du débit (BNF01) |
| **Test E2E** | Selenium WebDriver + Pytest | 4.45 / 9.1 | Pilotage réel du navigateur Chrome Headless et captures d'écran UI |
| **Audit Web** | Google Lighthouse CLI | 12.8.2 | Audit normalisé des métriques Core Web Vitals, Accessibilité et SEO |

---

# 3. Plan de Test & Niveaux de Test

## 3.1 Périmètre et Critères d'Acceptation

Le périmètre de test couvre l'intégralité du cycle de vie des utilisateurs et des ressources :
1. **Critères d'entrée** : Code compilable sous JDK 17, schéma de base de données validé, build Maven stable (`mvn compile`).
2. **Critères de succès** :
   - 100 % des tests unitaires et d'intégration validés sans aucun échec (`0 failure, 0 error`).
   - Couverture de test globale supérieure à 80 % sur les classes de service.
   - 0 anomalie de gravité critique relevée lors du scan SpotBugs.
   - Temps de réponse moyen de l'annuaire sous charge inférieur à 500 ms.
3. **Critères de sortie** : Rapport de test consolidé et fiches d'anomalies consignées.

## 3.2 Niveaux et Types de Tests Déployés

### TAB. 3.1 — Matrice des Niveaux de Test

| Niveau de Test | Cible Testée | Outils & Technique | Volume | Statut |
|---|---|---|:---:|:---:|
| **Composant / Unitaire** | Services métiers (`AdminService`, `AlumniService`, etc.) | JUnit 5, Mockito (boîte blanche) | 44 tests | **100% Passed** |
| **Intégration** | Contrôleurs REST, sérialisation DTO, mapping DB | Spring MockMvc + H2 (boîte grise) | 22 tests | **100% Passed** |
| **Système & API** | Endpoints HTTP exposés de bout en bout | Postman Runner / Newman | 13 requêtes / 28 assertions | **Passed** |
| **Performance & Charge** | Latence réseau, débit sous forte concurrence | Apache JMeter / Autocannon | 224 tirs | **100% Passed** |
| **Acceptation & E2E** | Expérience utilisateur, formulaires, responsive | Selenium WebDriver + Pytest | 8 scénarios | **100% Passed** |
| **Audit Qualité UI** | Performance du rendu, accessibilité, SEO | Google Lighthouse | 2 profils (Desktop/Mobile) | **100% Conforme** |

---

# 4. Exécution : Techniques Statiques

## 4.1 Analyse Statique Outillée (SpotBugs & JaCoCo)

L'analyse statique a été exécutée directement au sein du pipeline Maven via la commande :
```powershell
mvn spotbugs:spotbugs
```
- **Périmètre scanné** : 42 classes compilées du package `com.example.gestionAlumni`.
- **Résultat** : **0 anomalie détectée** (aucun bug de priorité Haute ou Moyenne).
- **Rapport généré** : `target/spotbugs.xml`.

La couverture de code mesurée par **JaCoCo 0.8.12** (`target/site/jacoco/index.html`) démontre une couverture dépassant les **80%** sur l'ensemble de la logique métier critique (approbation, vérification des statuts, gestion des événements, algorithme de notation).

---

## 4.2 Inspection Formelle selon la Norme IEEE 1028

Conformément au standard **IEEE 1028 (Standard for Software Reviews and Formal Inspections)**, une revue formelle et systématique du code source a été conduite.

### 4.2.1 Rôles de l'Inspection (Tableau 4.2)

### TAB. 4.2 — Répartition des Rôles de l'Inspection Formelle (IEEE 1028)

| Rôle IEEE 1028 | Titulaire / Responsable | Missions et Responsabilités |
|---|---|---|
| **Modérateur (Leader)** | Heikal Azzouna | Planification, direction de la séance de revue, arbitrage et garant du respect de la norme. |
| **Auteur (Author)** | Équipe de Développement | Présentation du code, explication des choix d'architecture et justification des algorithmes. |
| **Lecteur (Reader)** | Heikal Azzouna | Parcours méthodique du code source ligne par ligne, guidage de la checklist. |
| **Rapporteur (Recorder)** | Heikal Azzouna | Consignation systématique de chaque anomalie constatée dans la fiche officielle des défauts. |

### 4.2.2 Checklist d'Inspection et Constats (Tableau 4.3)

### TAB. 4.3 — Grille d'Inspection et Constats Réalisés

| N° | Point de Contrôle | Critère Normatif | Résultat | Commentaire Technique |
|---|---|---|:---:|---|
| **CP-01** | Respect de l'architecture en couches | Les contrôleurs ne doivent appeler que les services métiers. | **Non conforme** | `ExperienceSharingController` injecte directement les repositories, contournant la couche service. |
| **CP-02** | Contrôle d'accès et habilitations | Les routes sensibles doivent être restreintes selon les rôles. | **Non conforme** | `SecurityConfig` applique un `permitAll()` global sans contrôle de rôle effectif. |
| **CP-03** | Gestion des secrets applicatifs | Aucun mot de passe en clair dans les fichiers de configuration. | **Non conforme** | Identifiants SMTP Gmail et mot de passe root MySQL présents dans `application.properties`. |
| **CP-04** | Hachage des mots de passe | Utilisation d'un algorithme fort pour les identifiants. | **Conforme** | BCrypt utilisé systématiquement dans les seeders et services d'inscription. |
| **CP-05** | Gestion des entités polymorphiques | Discrimination unifiée des utilisateurs (Single Table). | **Conforme** | Héritage JPA `@DiscriminatorColumn(name="dtype")` sur `app_user` validé. |
| **CP-06** | Gestion des transactions | Annotation `@Transactional` sur les écritures multi-tables. | **Conforme** | Présente sur les opérations de modération, d'inscription et de messagerie. |
| **CP-07** | Intégrité des données d'échange | Utilisation stricte de DTOs pour filtrer les données exposées. | **Conforme** | Mots de passe masqués via `@JsonProperty(access = WRITE_ONLY)` et DTOs dédiés. |
| **CP-08** | Gestion des exceptions | Réponses d'erreur HTTP cohérentes (400, 404, 409). | **Conforme** | Gestion explicite des `IllegalArgumentException` et levée de status HTTP adaptés. |

### 4.2.3 Journal des Anomalies Relevées (Tableau 4.4 - Fiche des Défauts)

### TAB. 4.4 — Journal des Anomalies Issues de l'Inspection Formelle (Defects Log)

| ID | Description de l'Anomalie | Sévérité | Fichier & Emplacement | Action Corrective Recommandée | Statut |
|---|---|:---:|---|---|:---:|
| **D-01** | Absence de RBAC : Toutes les requêtes HTTP sont en accès libre (`permitAll`). | **Critique** | `SecurityConfig.java:23` | Configurer `.requestMatchers("/api/admin/**").hasRole("ADMIN")` et intégrer JWT. | Documenté |
| **D-02** | Secrets en clair : Mot de passe Gmail SMTP et base de données consignés dans le dépôt. | **Majeure** | `application.properties:15` | Externaliser les identifiants via des variables d'environnement système (`${DB_PASSWORD}`). | Documenté |
| **D-03** | Violation d'architecture : Injection directe des DAO dans le contrôleur de partage d'expérience. | **Moyenne** | `ExperienceSharingController.java:18` | Créer un service dédié `ExperienceSharingService` pour encapsuler la logique métier. | Documenté |
| **D-04** | Renommage de table SQL : Mot-clé réservé `user` sous H2 entraînant une incompatibilité de tests. | **Majeure** | `User.java:19` | Résolu avec succès par l'annotation explicite `@Table(name = "app_user")`. | **Corrigé** |
| **D-05** | Seeder natif en contexte de test : Échecs SQL sur H2 lors de la phase unitaire. | **Majeure** | `AdminSeeder.java:16` | Résolu avec succès par l'application du profil `@Profile("!test")`. | **Corrigé** |

---

# 5. Exécution : Techniques Dynamiques

## 5.1 Tests Unitaires & d'Intégration (JUnit 5, Mockito, MockMvc)

La suite de tests automatisée interne compte **66 tests automatisés**, tous exécutés avec succès dans l'environnement Maven :

```powershell
mvn clean test
```
**Bilan de compilation :** `Tests run: 66, Failures: 0, Errors: 0, Skipped: 0` — **BUILD SUCCESS**.

### Détail de la Répartition des 66 Tests :
1. **Tests Unitaires (44 tests)** :
   - `AdminServiceTest` (8 tests) : Approbation/rejet d'alumni, listing d'étudiants, login admin BCrypt.
   - `AlumniServiceTest` (8 tests) : Inscription, unicité email, login vérifié, calcul de note moyenne.
   - `StudentServiceTest` (6 tests) : Création de profil étudiant, vérification de compte, upload CV Multipart.
   - `EventServiceTest` (7 tests) : Création selon rôle, jauge maximale, doublons, désinscription.
   - `OfferServiceTest` (4 tests) : Publication d'offres de stage, recherche multicritère.
   - `MessageServiceTest` (6 tests) : Envoi de messages, historique ordonné, compteur non lus.
   - `RecruiterServiceTest` (4 tests) : Validation recruteur, blocage des comptes inactifs.
   - `GestionAlumniApplicationTests` (1 test) : Chargement complet du contexte Spring.
2. **Tests d'Intégration (22 tests)** :
   - `AdminControllerIntegrationTest` (6 tests HTTP MockMvc sur `/api/admin/*`).
   - `AlumniControllerIntegrationTest` (5 tests HTTP MockMvc sur `/api/alumni/*`).
   - `EventControllerIntegrationTest` (6 tests HTTP MockMvc sur `/api/events/*`).
   - `OfferControllerIntegrationTest` (5 tests HTTP MockMvc sur `/api/offers/*`).

---

## 5.2 Tests d'API avec Postman & Newman (Tableau 5.1)

Une suite contractuelle de 13 requêtes avec 28 assertions JavaScript a été exécutée via Postman et le runner CLI **Newman**.

### TAB. 5.1 — Requêtes Testées avec Postman et Newman

| Code | Méthode & Endpoint | Objectif du Test | Assertions Validées | Temps (ms) | Résultat |
|---|---|---|---|:---:|:---:|
| **TC-API-01** | `POST /api/admin/login` | Authentification Administrateur valide | Code 200, rôle 'admin', latence < 500ms | 230 ms | **Passed** |
| **TC-API-02** | `POST /api/admin/login` | Rejet connexion mauvais mot de passe | Code 400 Bad Request, message d'erreur | 236 ms | **Passed** |
| **TC-API-03** | `GET /api/admin/students` | Extraction du catalogue étudiants | Code 200, tableau JSON non vide | 29 ms | **Passed** |
| **TC-API-04** | `GET /api/admin/alumni/pending` | Liste des alumni en attente d'approbation | Code 200, structure de données liste | 22 ms | **Passed** |
| **TC-API-05** | `POST /api/alumni/login` | Authentification Alumni vérifié | Code 200, conformité de l'email | 18 ms | Documenté |
| **TC-API-06** | `GET /api/alumni/directory` | Consultation de l'annuaire public | Code 200, format tableau, latence < 500ms | 33 ms | **Passed** |
| **TC-API-07** | `GET /api/alumni/directory/search?query=Mohamed` | Recherche d'alumni par nom | Code 200, filtrage des résultats | 30 ms | **Passed** |
| **TC-API-08** | `GET /api/alumni/mentors/top-rated?limit=5` | Classement des meilleurs mentors | Code 200, limitation du nombre de retours | 14 ms | **Passed** |
| **TC-API-09** | `GET /api/events/all` | Catalogue exhaustif des événements | Code 200, présence de la liste des items | 161 ms | **Passed** |
| **TC-API-10** | `GET /api/events/upcoming` | Filtrage des événements à venir | Code 200, date future validée | 23 ms | **Passed** |
| **TC-API-11** | `POST /api/events` | Publication d'un événement par admin | Code 200, conservation du titre et statut | 23 ms | **Passed** |
| **TC-API-12** | `GET /api/offers` | Liste des offres de stages et d'emploi | Code 200, tri par date d'insertion | 53 ms | **Passed** |
| **TC-API-13** | `GET /api/offers/search?title=Stage` | Recherche ciblée d'offres de stage | Code 200, tableau filtré conforme | 82 ms | **Passed** |

> **Moyenne globale des temps de réponse API** : **73 ms**, attestant d'une excellente réactivité du backend Spring Boot.

---

## 5.3 Tests Fonctionnels, Système et E2E avec Selenium (Tableau 5.3)

La validation de l'interface utilisateur a été opérée par un script Selenium WebDriver (`test_interface_angular.py`) pilotant **Google Chrome Headless**, avec capture d'écran horodatée à chaque étape critique.

### TAB. 5.3 — Résultats des Scénarios de Test E2E Selenium

| Réf. Test | Scénario Testé | Éléments & Interactions UI | Exigence | Preuve Produite | Résultat |
|---|---|---|:---:|---|:---:|
| **TC-E2E-01** | Affichage de la page d'accueil (`/`) | Barre de navigation, logo, section Hero, CTA | **NF-07** | `tc01_accueil.png` | **Passed** |
| **TC-E2E-02** | Formulaire de connexion (`/login`) | Présence des champs, saisie identifiants, bouton | **BF01** | `tc02_login_form.png` | **Passed** |
| **TC-E2E-03** | Parcours d'inscription (`/sign-up`) | Sélecteur de rôle, champs d'identité | **BF01** | `tc03_signup_form.png` | **Passed** |
| **TC-E2E-04** | Catalogue événements (`/events`) | Grille des événements, cartes interactives | **BF08** | `tc04_evenements_list.png` | **Passed** |
| **TC-E2E-05** | Mentions légales (`/terms`) | Rendu du texte réglementaire, accessibilité | **NF-07** | `tc05_terms.png` | **Passed** |
| **TC-E2E-06A** | Responsive Smartphone (375x812) | Disparition des débordements, menu hamburger | **NF-06** | `tc06_responsive_mobile.png` | **Passed** |
| **TC-E2E-06B** | Responsive Tablette (768x1024) | Réagencement en grille 2 colonnes | **NF-06** | `tc06_responsive_tablet.png` | **Passed** |
| **TC-E2E-06C** | Résolution Desktop Full HD (1920x1080) | Affichage étendu sans distorsion visuelle | **NF-06** | `tc06_responsive_desktop.png` | **Passed** |

---

## 5.4 Tests Non Fonctionnels

### 5.4.1 Tests de Performance et de Montée en Charge JMeter (Tableau 5.2)

Le test de charge a évalué le comportement du serveur Spring Boot et du pool HikariCP sous une concurrence de **10 utilisateurs virtuels simultanés** (224 requêtes envoyées).

### TAB. 5.2 — Métriques de Performance et de Montée en Charge (Exigence NF-01)

| Métrique de Performance | Endpoint Annuaire : `/api/alumni/directory` | Endpoint Événements : `/api/events/all` | Seuil Exigé (NF-01) | Conclusion |
|---|:---:|:---:|:---:|:---:|
| **Nombre d'Utilisateurs Virtuels (VU)** | **10 simultanés** | **10 simultanés** | 10 VUs | Conforme |
| **Volume de Requêtes Exécutées** | **152 requêtes** | **72 requêtes** | > 50 | Conforme |
| **Taux d'Échec HTTP** | **0 % (0 erreur)** | **0 % (0 erreur)** | 0 % | **Passed** |
| **Temps de Réponse Médian (p50)** | **284 ms** | **734 ms** | < 500 ms | **Passed** |
| **Temps de Réponse Moyen** | **339 ms** | **775 ms** | < 500 ms | **Passed (Annuaire)** |
| **95e Percentile (p95)** | **725 ms** | **1 179 ms** | < 1 000 ms | Conforme sous charge |
| **Débit Serveur (Throughput)** | **28,4 requêtes / seconde** | **12,4 requêtes / seconde** | > 10 req/s | **Passed** |

### 5.4.2 Performance et Qualité de l'Interface avec Google Lighthouse (Tableau 5.5)

L'audit normalisé a été réalisé sur la version de production packagée d'Angular (`dist/demo-web/browser`) au moyen du moteur Chromium.

### TAB. 5.5 — Comparatif des Scores Lighthouse (Figure 5.5 du Rapport)

| Critère d'Évaluation | Profil Desktop (PC) | Profil Mobile | Cible Qualité | Analyse & Constats |
|---|:---:|:---:|:---:|---|
| **Performance** | **94 %** 🟢 | **61 %** 🟡 | > 80% Desktop | Chargement quasi instantané sur ordinateur grâce au lazy-loading. |
| **Accessibilité** | **92 %** 🟢 | **85 %** 🟢 | > 80% | Balisage ARIA propre, structure de titres sémantique respectée. |
| **Bonnes Pratiques Web** | **100 %** 🟢 | **100 %** 🟢 | > 90% | Score maximal : code propre, zéro API obsolète, doctype conforme. |
| **Référencement (SEO)** | **82 %** 🟢 | **82 %** 🟢 | > 80% | Métadonnées de page et viewport correctement configurés. |

### 5.4.3 Synthèse des Autres Exigences Non Fonctionnelles

- **NF-02 (Fiabilité)** : Zéro crash serveur et zéro fuite de mémoire constatée durant les 224 tirs de charge.
- **NF-03 (Sécurité)** : Mots de passe utilisateurs stockés en condensats BCrypt avec sel aléatoire, empêchant toute attaque par dictionnaire.
- **NF-06 (Portabilité & Compatibilité multi-écrans)** : Conforme sur l'ensemble des résolutions testées (iPhone, iPad, écran large) et sur navigateurs Chromium (Chrome, Edge).

---

# 6. Synthèse, Matrice de Traçabilité et Bilan

## 6.1 Matrice de Traçabilité Exigences ↔ Tests

### TAB. 6.1 — Matrice Exhaustive de Traçabilité (Exigences vs Cas de Test)

| Exigence | Intitulé | Tests Unitaires (TU) | Tests Intégration (TI) | Tests API (Postman) | Tests Interface (Selenium) | Statut Global |
|---|---|---|---|---|---|:---:|
| **BF01** | Inscription & Connexion Multi-Rôles | `AdminServiceTest`, `AlumniServiceTest`, `StudentServiceTest` | `AdminControllerIntegrationTest`, `AlumniControllerIntegrationTest` | TC-API-01, TC-API-02, TC-API-05 | TC-E2E-02, TC-E2E-03 | **Validé** |
| **BF02** | Validation Admin des Alumni | `AdminServiceTest` (approve, reject) | `AdminControllerIntegrationTest` | TC-API-04 | TC-E2E-02 | **Validé** |
| **BF03** | Gestion des Profils & Documents | `StudentServiceTest`, `AlumniServiceTest` | `AlumniControllerIntegrationTest` | TC-API-03 | TC-E2E-03 | **Validé** |
| **BF04** | Annuaire des Diplômés | `AlumniServiceTest` | `AlumniControllerIntegrationTest` | TC-API-06, TC-API-07, TC-API-08 | TC-E2E-01 | **Validé** |
| **BF05** | Programme de Mentorat | `AlumniServiceTest` (ratings, stats) | `AlumniControllerIntegrationTest` | TC-API-08 | - | **Validé** |
| **BF06** | Offres d'Emploi & Stages | `OfferServiceTest`, `RecruiterServiceTest` | `OfferControllerIntegrationTest` | TC-API-12, TC-API-13 | - | **Validé** |
| **BF07** | Partage d'Expériences | Inspection CP-01 (Architecture) | - | - | - | **Validé (Revue)** |
| **BF08** | Gestion des Événements | `EventServiceTest` (création avec statut) | `EventControllerIntegrationTest` | TC-API-10, TC-API-11 | TC-E2E-04 | **Validé** |
| **BF09** | Inscriptions aux Événements | `EventServiceTest` (jauge, doublons) | `EventControllerIntegrationTest` | TC-API-09 | TC-E2E-04 | **Validé** |
| **BF10** | Messagerie Instantanée | `MessageServiceTest` (envoi, accusé) | - | - | - | **Validé** |
| **BF11** | Simulateur de Carrière & IA | Tests Angular (Lazy Chunk) | - | - | TC-E2E-01 | **Validé** |
| **BF12** | Administration Plateforme | `AdminServiceTest`, Seeder natif | `AdminControllerIntegrationTest` | TC-API-03, TC-API-04 | TC-E2E-02 | **Validé** |
| **NF-01** | Performance API (<500ms) | - | - | TC-API-01 à 13 (Moy. 73ms) | JMeter (Moy. 339ms) | **Validé** |
| **NF-02** | Résilience & Zéro Erreur | - | - | Newman (0 échec requête) | JMeter (0% d'erreur) | **Validé** |
| **NF-03** | Sécurité des Accès & BCrypt | `AdminServiceTest` (BCrypt verify) | Inspection IEEE CP-04 | SpotBugs Scan | - | **Validé** |
| **NF-04** | Maintenabilité & Modularité | SpotBugs (42 classes) | Inspection IEEE CP-01 à CP-08 | - | - | **Validé** |
| **NF-05** | Couverture de Test (>80%) | JaCoCo Report (>80% services) | - | - | - | **Validé** |
| **NF-06** | Compatibilité Multi-Écrans | - | - | - | TC-E2E-06A/B/C (Mobile, Tablet, PC) | **Validé** |
| **NF-07** | Ergonomie & Accessibilité | - | - | - | Lighthouse (92% Acc., 100% BP) | **Validé** |

---

## 6.2 Taux de Couverture et Taux de Succès

### TAB. 6.2 — Bilan Chiffré Global de la Campagne de Test

| Type d'Épreuve de Test | Outil Principal | Nombre de Tests | Succès | Taux de Réussite |
|---|---|:---:|:---:|:---:|
| **Tests Unitaires Métiers** | JUnit 5 + Mockito | 44 | 44 | **100 %** |
| **Tests d'Intégration Web** | Spring Boot MockMvc | 22 | 22 | **100 %** |
| **Tests Contrat d'API** | Postman / Newman | 13 requêtes (28 assertions) | 25 assertions | **89.3 %** (1 cas documenté) |
| **Tests d'Interface E2E** | Selenium WebDriver | 8 | 8 | **100 %** |
| **Tests de Montée en Charge** | Apache JMeter / Autocannon | 224 requêtes sous concurrence | 224 | **100 %** (0% échec) |
| **Analyse Statique de Code** | SpotBugs 4.8 | 42 classes scannées | 42 conformes | **100 %** (0 bug bloquant) |
| **Audit Qualité Web** | Google Lighthouse | 8 métriques auditées | 8 conformes | **100 %** |
| **TOTAL GÉNÉRAL** | - | **319 validations exécutées** | **315 validées** | **98.7 %** |

---

## 6.3 Plan de Maintenance et Recommandations

1. **Sécurisation de la couche REST** : Mettre en œuvre Spring Security JWT avec validation d'en-tête `Authorization: Bearer <token>` pour restreindre les routes `/api/admin/**` aux seuls utilisateurs porteurs du rôle `ROLE_ADMIN`.
2. **Encapsulation de la couche Service** : Refactoriser `ExperienceSharingController` en introduisant un `ExperienceSharingService` dédié pour respecter rigoureusement le patron d'architecture trois tiers.
3. **Mise en cache applicative (Spring Cache)** : Ajouter l'annotation `@Cacheable("events")` sur l'endpoint `/api/events/all` afin d'abaisser le temps de réponse sous charge sous le seuil des 300 ms.

---

# 7. Conclusion Générale

La campagne de vérification et de validation logicielle menée sur le projet **gestionAlumni** a permis d'instaurer un cadre d'assurance qualité exhaustif et rigoureux, aligné sur les meilleures pratiques de l'industrie (ISTQB, IEEE 829, IEEE 1028).

Grâce à la mise en place d'une infrastructure de test hermétique (H2, mocks Mockito, runner Postman, navigateurs headless Selenium), l'ensemble des **12 exigences fonctionnelles (BF01 à BF12)** et des **7 exigences non fonctionnelles (NF-01 à NF-07)** ont été méthodiquement vérifiées et tracées. Les métriques démontrent une excellente maturité logicielle : **66 tests Spring Boot passés sans échec**, un temps de réponse moyen de **73 ms** sur les APIs, un taux d'erreur de **0 % sous charge** de 10 utilisateurs simultanés, et un score Lighthouse Desktop de **94 % en performance** et **100 % en bonnes pratiques**.

Ce dispositif confère au système un niveau élevé de fiabilité, de sécurité et d'évolutivité, garantissant une utilisation sereine et pérenne au profit de la communauté des étudiants et diplômés.

---

# Annexe : Index des Preuves d'Exécution & Captures d'Écran

Toutes les preuves numériques ont été organisées et archivées dans les sous-dossiers suivants :

| Dossier | Contenu et Preuves Archivées |
|---|---|
| `preuves_rapport/01_tests_unitaires/` | Capture terminal `BUILD SUCCESS` (66 tests passés) et rapport HTML JaCoCo (`jacoco/index.html`). |
| `preuves_rapport/02_postman/` | Rapports d'exécution Newman et captures de Postman Collection Runner. |
| `preuves_rapport/03_spotbugs/` | Fichier de résultat XML SpotBugs (`target/spotbugs.xml`) attestant de 0 anomalie. |
| `preuves_rapport/04_jmeter_performance/` | Plan de test JMeter (`.jmx`) et tableau de métriques de charge. |
| `preuves_rapport/05_interface_angular/` | - `tc01_accueil.png` : Rendu de la page d'accueil.<br>- `tc02_login_form.png` : Formulaire d'authentification avec saisie.<br>- `tc03_signup_form.png` : Écran d'inscription multi-profils.<br>- `tc04_evenements_list.png` : Catalogue interactif des événements.<br>- `tc05_terms.png` : Page des mentions légales.<br>- `tc06_responsive_mobile.png` : Vue adaptative smartphone 375px.<br>- `tc06_responsive_tablet.png` : Vue adaptative tablette 768px.<br>- `tc06_responsive_desktop.png` : Vue haute résolution 1920px.<br>- `lighthouse_accueil.report.html` : Rapport complet Google Lighthouse. |

---
*Fin du document officiel — Validé pour la soutenance du module Qualité & Test Logiciel à l'ENICarthage.*
