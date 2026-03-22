📄 Question Paper Generator

📌 Project Description

The Question Paper Generator is a full-stack web application designed to automate the creation of examination question papers from a structured question bank.

The system allows educators to generate balanced and customized question papers based on constraints such as units, marks distribution, and Bloom’s taxonomy levels, reducing manual effort and ensuring fairness in assessment.

🚀 Key Features

📂 Question Bank Management
Upload questions via Excel/CSV
Store questions with attributes:
Subject
Unit
Marks
Bloom Level (1–4)

⚙️ Custom Paper Generation
Select subject
Define unit-wise distribution
Choose Bloom’s taxonomy levels
Specify marks allocation

🧠 Smart Selection Algorithm
Dynamically selects questions based on constraints
Automatically adjusts if required questions are unavailable
Avoids duplication of questions

📑 Structured Question Paper Output
Generates properly formatted question papers
Ready for academic use

📥 PDF Generation (if implemented)
Export question paper in printable format


🏗️ Tech Stack
Frontend
React (Vite)
HTML, CSS, JavaScript
Backend
Java
Spring Boot
Database
MySQL


🔄 System Workflow
Upload question bank (Excel/CSV)
Store questions in database
User selects:
Subject
Units
Bloom levels
Marks distribution
Backend processes constraints
Questions are filtered and selected
Final question paper is generated
(Optional) Export as PDF


🧠 Core Logic
The system ensures:

✔ Balanced distribution across units
✔ Bloom’s taxonomy-based filtering
✔ No repeated questions
✔ Intelligent fallback to lower levels if needed

This makes the generated paper academically structured and fair, unlike simple random selection systems.

📊 Use Cases
Colleges generating semester exams
Schools conducting internal tests
Coaching centers preparing mock exams
Faculty automating paper creation


💡 Advantages
⏱ Saves time for teachers
🎯 Ensures balanced question papers
❌ Reduces human error
📈 Scalable for large question banks
🧑‍🏫 Easy to use
🛠️ Installation & Setup
# Clone the repository
git clone https://github.com/VarshiniAshokkumar/QuestionPaperGenerator.git

# Backend setup
cd backend
mvn spring-boot:run

# Frontend setup
cd frontend
npm install
npm run dev
🔮 Future Enhancements
🤖 AI-based question generation
📊 Analytics dashboard (unit coverage, difficulty analysis)
🔐 User authentication (Admin/Faculty roles)
☁️ Cloud deployment
📝 Support for multiple question formats (MCQ, descriptive)


👩‍💻 Author
Varshini Ashokkumar

📌 Conclusion

The Question Paper Generator simplifies the traditional examination process by automating paper creation with intelligent constraints. It enhances efficiency, accuracy, and consistency, making it a valuable solution for modern educational institutions.
