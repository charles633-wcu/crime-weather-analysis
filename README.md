
FOR LATER:::

(resume stuff)
Crime & Weather Insight Analytics Platform
• Designed and implemented a multi-service analytics platform in Java to analyze relationships between daily temperature and NYC shooting incidents (2006–2024).
• Built RESTful APIs using SparkJava to expose daily summaries, incident-level queries, and statistical analytics.
• Developed a backend analytics service to compute linear regression metrics (slope, correlation coefficient, R²) and served results via API endpoints.
• Containerized services using Docker and orchestrated them with Docker Compose, enabling reproducible local and cloud deployments.
• Deployed the system to AWS, hosting backend services on EC2 and a static frontend dashboard on S3 Static Website Hosting.

ADD THESE TO CLOUD , backend, infra roles
• Configured an API gateway (Apache APISIX) to route requests between services and handle browser CORS behavior.
• Managed cloud networking concepts including security groups, exposed ports, and container networking on AWS EC2.
• Implemented static site generation and decoupled frontend hosting from backend compute to improve scalability and reliability.

AI READER SKILLS
Java, REST APIs, SparkJava, Docker, Docker Compose, AWS EC2, AWS S3, API Gateway, SQL, SQLite, Data Analytics, Statistical Analysis, Cloud Deployment

one liner
Built and deployed a Java-based analytics platform using Docker and AWS to visualize and analyze relationships between temperature and crime data via REST APIs and a static dashboard.

ultra ats version
• Built Java REST APIs and analytics services for temperature and crime data analysis.
• Containerized services with Docker and deployed to AWS EC2 with a static frontend hosted on S3.

-------------------------

##Beginner / class project (you now)

S3 static site

EC2 API

HTTP

Different URLs

Config-based API base


##Intermediate

CloudFront

One domain

HTTPS

Path-based routing


##Production

CloudFront + ALB

Auto-scaling

WAF

CI/CD


MAYBE DELETE
Security & HTTPS

The application currently runs over HTTP for development and demonstration purposes.
In a production deployment, HTTPS would be terminated at a CDN or load balancer (e.g., AWS CloudFront or ALB), with traffic forwarded internally to the API gateway and services over HTTP. This mirrors standard industry practice and avoids embedding TLS logic inside application containers.


Link  
http://crime-temperature-dashboard.s3-website-us-east-1.amazonaws.com/index.html
MAYBE MAKE A CHART FOR A HISTORICAL INCIDENTS MOVING AVERAGE

WRITE (FOR INTERVIEW IN A SCALABLE PROJECT THERE WOULD BE A SEPERATE DATABASE CONTAINER, FRONT END DYNAMICALLY SERVED FROM AN API. BUT THIS PROJECT IS STATIC SO ITS JUST A DEMONSTRATION
# Crime & Weather Insight Analytics

## Overview

This project explores the relationship between **daily maximum temperature** and **shooting incidents in New York City** using historical data from **2006–2024**. It combines data ingestion, backend analytics, API routing, and a frontend dashboard to provide both **descriptive** and **statistical** views of the data.

The goal of the project is **not prediction**, but rather to **visualize patterns and associations** between temperature and incident frequency in a clear, defensible way. Emphasis is placed on interpretability, reproducibility, and clean system architecture rather than black-box modeling.

---

## Key Features

### Interactive Analytics Dashboard

#### Trend View
Displays a smoothed average of daily shooting incidents as a function of temperature.

- Transparent bars show coarse averages across fixed temperature ranges (5°F bins)
- A smoothed line shows a rolling average across nearby temperature values
- Designed to highlight overall patterns without imposing a parametric model

#### Scatter View with Regression
- Each point represents a single day (temperature vs. total incidents)
- A global linear regression line is overlaid for reference
- Used to assess direction and strength of association, not causation

---

### Search by Date

- Search for any date between **January 1, 2006 and December 31, 2024**
- Displays:
  - Daily high temperature
  - Total incidents
  - A formatted list of incidents including borough, precinct, and demographic fields when available

---

### Statistical Summary

- Regression slope (β)
- Correlation coefficient (r)
- Coefficient of determination (R²)
- Plain-language interpretation of the slope  

---

## Data Sources

- **City of NYC Open Data API**  
  Public dataset containing shooting incidents with date, location, and demographic attributes.

- **Open-Meteo Historical Weather API**  
  Daily maximum temperature values matched by date.

The dataset includes **29,745 recorded shooting incidents** across the study period.

---

## Methodology Notes

### Trend (Smoothed Average) Chart
- Computes a **rolling average over temperature**, not time
- Uses:
  - 1°F step size
  - ±1°F averaging window
  - Minimum sample size threshold to avoid unstable estimates at temperature extremes
- Purpose: descriptive visualization of how average incident frequency varies with temperature

### Scatter + Regression Chart
- Uses all daily observations
- Linear regression parameters are computed once on the backend

### Important Caveats
- Temperature explains only a portion of daily variation
- Many social, temporal, and environmental factors influence incident rates
- Results should be interpreted as **associational**, not causal

---

## Implementation Overview

This project is organized as a small, service-oriented system that separates **data ingestion**, **data access**, **analytics**, and **presentation** concerns.

---

### Data Ingestion (`data-loader`)

The data loader module is responsible for:
- importing historical NYC shooting incident data
- importing daily maximum temperature data
- normalizing and storing records in a SQLite database

This step is designed to be run once (or periodically) and is decoupled from runtime analytics and visualization services.

---

### Data Service API (`data-service`)

The data service exposes read-only REST endpoints for accessing structured data from the database, including:
- daily summaries (incident count and temperature by date)
- incident-level records for a given date

This service acts as the **single source of truth** for raw and aggregated data used throughout the project.

---

### Analytics Service (`app-service`)

The analytics service performs higher-level statistical computation on top of the data service, including:
- computing global linear regression parameters
- returning summary statistics (slope, correlation coefficient, R²)

Regression calculations are performed once on the backend and reused by the frontend, ensuring consistency and avoiding unnecessary recomputation in the browser.

---

### API Gateway (`conf`)

An API gateway (Apache APISIX) is used to:
- provide a single public entry point for backend services
- route requests to the appropriate service based on URL path
- handle CORS for browser-based access

This mirrors common production patterns used in microservice-based systems.

---

### Static Site Generation (`site-generator`)

The site generator builds a static frontend that:
- fetches live data from backend APIs
- renders interactive visualizations using Chart.js
- provides search and filtering functionality

The generated files are served independently from backend compute resources, keeping the runtime architecture lightweight and scalable.

---

### Frontend Dashboard (`web`)

The frontend dashboard presents:
- a smoothed temperature–incident trend visualization
- a scatter plot with regression overlay
- interactive date-based lookup of daily summaries and incidents

All visualizations are generated dynamically in the browser using API responses, with no server-side rendering.

---

## Deployment (AWS)

The application is deployed using **Amazon Web Services** with a clear separation between compute and static content:

- Backend services and the API gateway run on **AWS EC2**, containerized using **Docker Compose**
- EC2 security groups are configured to expose only required public ports
- Internal service communication occurs over Docker networking
- The static dashboard is hosted using **Amazon S3 Static Website Hosting**
- The frontend fetches live data from EC2-hosted APIs at runtime
(IS TAHT TRUE? MAYBE MENTION THAT FOR A LIVE SERVICE IT WOULD BE A BIT DIFFERENT, RDS WOULD HOST INDUSTRY PROJECTS)

This deployment reflects real-world practices where APIs run on compute infrastructure and frontends are served as static assets.

### HTTPS Note
For demonstration purposes, services are exposed over HTTP. In a production deployment, HTTPS would be terminated at a CDN or load balancer (e.g., AWS CloudFront or an Application Load Balancer), with traffic forwarded internally to the API gateway over HTTP.

---

## Project Structure

crime-weather-analysis/
├── data-loader/        # Data ingestion and database population
├── data-service/       # API for summaries and incident queries
├── app-service/        # Analytics and regression endpoints
├── site-generator/     # Static dashboard generation
├── conf/               # API gateway configuration
├── web/                # Static frontend entry
├── docker-compose.yml
└── README.md
#   c r i m e - w e a t h e r - a n a l y s i s  
 