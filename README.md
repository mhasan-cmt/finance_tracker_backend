# Personal Finance Tracker

## Project Overview
The **Personal Finance Tracker** is an Android application designed to help users efficiently manage their finances. It allows users to track their income, expenses, budgets, and savings while providing AI-based smart financial insights.

This project is being developed as part of our **Software Development** course.

## Features
- **User Authentication** (JWT, Spring Security, Email verification with resend OTP feature)
- **Expense & Income Tracking**
- **Budgeting & Savings Management**
- **AI-Based Financial Insights & Forecasting**
- **Onboarding Screen for New Users**
- **Google Sign-In Integration**
- **Accessibility Features (Voice Assistant, Screen Reader)**
- **(Will update)**

## Tech Stack
- **Backend:** Spring Boot (Java), PostgreSQL
- **Frontend:** Android (Java)
- **Local Storage:** Room Database

## Project Proposal and Literature Survey
You can find our literature survey about the project [here](https://drive.google.com/file/d/1XR4gw4n2Ps2t83rJIoZgNWK5rT9JN_Bm/view?usp=sharing).<br>
You can find the detailed project proposal [here](https://drive.google.com/file/d/1y0h2Wgg-xgp4-WI7U7ZmWDAlf307NRYc/view?usp=sharing).


## Installation & Setup
### Backend
1. Clone the repository:
   ```bash
   git clone https://github.com/mhasan-cmt/finance-tracker.git
   ```
2. Navigate to the backend directory:
   ```bash
   cd finance-tracker/backend
   ```
3. Set up PostgreSQL and configure the database in `application.yml`.
4. Configure email settings:
   - Create a `.env` file in the backend directory based on the `example.env` template
   - Set the following environment variables:
     ```
     DB_URL=jdbc:postgresql://localhost:5432/your_database_name
     DB_USER=your_database_username
     DB_PASSWORD=your_database_password
     GMAIL_USER=your_gmail_address@gmail.com
     GMAIL_APP_PASSWORD=your_gmail_app_password
     ```
   - **Important for Gmail users**: You need to use an App Password, not your regular Gmail password
     - Go to your [Google Account Security settings](https://myaccount.google.com/security)
     - Enable 2-Step Verification if not already enabled
     - Go to [App passwords](https://myaccount.google.com/apppasswords)
     - Select "Mail" as the app and "Other" as the device (name it "Finance Tracker")
     - Copy the generated 16-character password and use it as your `GMAIL_APP_PASSWORD`

5. Run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```

### Android App
1. Go this [repository](https://github.com/mhasan-cmt/fitrack-mobile-app) and clone the android project
2. Open the Android project in **Android Studio**.
3. Configure Firebase by adding the `google-services.json` file.
4. Build and run the application.

## Contribution Guidelines
1. Fork the repository.
2. Create a new feature branch.
3. Commit your changes and push them.
4. Submit a pull request for review.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact
For any queries, feel free to reach out to the team via email or GitHub issues.
