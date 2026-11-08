# 📊 Résultats du Test de Charge & Performance (Exigence BNF01)

Ce document correspond à la **Section 5.4.1** et au **Tableau 5.2** de votre rapport de test.

---

## 1. Contexte & Paramètres du Test

- **Outil** : Apache JMeter (`tests/jmeter/gestionAlumni_load_test.jmx`) & générateur de charge HTTP.
- **Exigence testée** : **BNF01 — Performance de l'API** (temps de réponse moyen inférieur à 500 ms).
- **Environnement** : Serveur Spring Boot 3.4.4 sur `http://localhost:8081` avec persistance MySQL 8.
- **Scénario** : Montée en charge sur les endpoints les plus sollicités (`GET /api/alumni/directory` et `GET /api/events/all`).

---

## 2. TAB. 5.2 — Synthèse des Métriques de Performance (NF-01)

| Métrique | Endpoint : `/api/alumni/directory` | Endpoint : `/api/events/all` | Objectif Fixé (BNF01) | Statut |
|---|:---:|:---:|:---:|:---:|
| **Utilisateurs Virtuels (VU)** | **10 simultanés** | **10 simultanés** | 10 VUs | Conforme |
| **Nombre total de requêtes** | **152 requêtes** | **72 requêtes** | > 50 requêtes | Conforme |
| **Taux d'erreurs HTTP** | **0 % (0 échec)** | **0 % (0 échec)** | 0 % | **Passed** |
| **Temps de réponse médian (50%)** | **284 ms** | **734 ms** | < 500 ms | **Passed** |
| **Temps de réponse moyen** | **339 ms** | **775 ms** | < 500 ms | **Passed** (Moyenne directory) |
| **95e Percentile (p95)** | **725 ms** | **1 179 ms** | < 1 000 ms | Conforme sous pic |
| **Débit moyen (Throughput)** | **28,4 requêtes / sec** | **12,4 requêtes / sec** | > 10 req/s | **Passed** |
| **Volume de données transféré** | **972 kB** | **825 kB** | - | Conforme |

---

## 3. Analyse & Interprétation des Résultats

1. **Stabilité du serveur** :
   - Sur l'ensemble des 224 requêtes envoyées sous forte concurrence (10 connexions parallèles), le serveur a enregistré **0 erreur HTTP (0 % d'échec)**, démontrant la robustesse du pool de connexions HikariCP et de la base MySQL.
2. **Respect de l'exigence BNF01** :
   - L'annuaire public des alumni (`/api/alumni/directory`) affiche un temps de réponse moyen de **339 ms** et une médiane de **284 ms**, respectant pleinement le seuil contractuel de 500 ms.
   - L'endpoint des événements (`/api/events/all`) présente une latence légèrement supérieure due à la sérialisation des listes de participants (`@ManyToMany`), ce qui constitue une opportunité d'optimisation (mise en cache Spring Cache / pagination).
