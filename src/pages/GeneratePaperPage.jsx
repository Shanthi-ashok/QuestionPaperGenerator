import React, { useState } from "react";
import { generatePaper } from "../api/paper";
import { generatePDF } from "../api/pdf";
import "../styles/GeneratePaperPage.css";
import Navbar from "../components/Navbar";

function GeneratePaperPage() {

  const [form, setForm] = useState({
    subjectCode: "",
    subjectTitle: "",
    department: "",
    yearSem: "",
    examType: "",
    date: ""
  });

  const [blooms, setBlooms] = useState({
    1: "",
    2: "",
    3: "",
    4: "",
    5: ""
  });

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value
    });

  };

  const handleBloomChange = (unit, value) => {

    setBlooms({
      ...blooms,
      [unit]: value
    });

  };

  const formatDate = (date) => {

    const parts = date.split("-"); // yyyy-mm-dd
    return `${parts[2]}-${parts[1]}-${parts[0]}`; // dd-mm-yyyy

  };

  const generate = async () => {

    try {

      // Filter bloom levels
      const filteredBlooms = Object.fromEntries(
        Object.entries(blooms).filter(([k,v]) => v !== "" && !isNaN(v))
      );

      Object.keys(filteredBlooms).forEach((key) => {
        filteredBlooms[key] = parseInt(filteredBlooms[key]);
      });

      console.log("Bloom Request:", filteredBlooms);

      // STEP 1: Generate Paper
      const paperRes = await generatePaper({

        subjectCode: form.subjectCode,
        examType: form.examType,
        numberOfSets: 1,
        unitBloomPreferences: filteredBlooms

      });

      console.log("Paper Response:", paperRes.data);

      const paper = paperRes.data;

      const paperId = paper.paperId || paper.id;

      if (!paperId) {

        alert("Paper ID missing from backend response");
        console.error(paper);
        return;

      }

      // Convert date format
      const formattedDate = formatDate(form.date);

      // STEP 2: Prepare PDF Body
      const pdfBody = {

        examType: form.examType,
        subjectTitle: form.subjectTitle,
        department: form.department,
        yearSem: form.yearSem,
        date: formattedDate,

        paperId: paperId,
        sets: paper.sets,
        subjectCode: form.subjectCode

      };

      console.log("PDF Request:", pdfBody);

      // STEP 3: Generate PDF
      const pdfRes = await generatePDF(pdfBody);

      const blob = new Blob([pdfRes.data], { type: "application/pdf" });

      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");

      link.href = url;
      link.download = "QuestionPaper.pdf";

      document.body.appendChild(link);

      link.click();

      link.remove();

    } catch (error) {

      console.error("Generation Error:", error.response?.data || error);

      alert("Generation Failed. Check console.");

    }

  };

  return (
    <>
      <Navbar />
      <div className="page-wrapper">
        <div className="card">
          <h2 className="page-title">Generate Question Paper</h2>

          <div className="form-group">
            <label>Subject Code</label>
            <input name="subjectCode" onChange={handleChange} />
          </div>

      <div className="form-group">
        <label>Subject Title</label>
        <input name="subjectTitle" onChange={handleChange}/>
      </div>

      <div className="form-group">
        <label>Department</label>
        <input name="department" onChange={handleChange}/>
      </div>

      <div className="form-group">
        <label>Year / Semester</label>
        <input name="yearSem" onChange={handleChange}/>
      </div>

      <div className="form-group">
        <label>Date</label>
        <input type="date" name="date" onChange={handleChange}/>
      </div>

      <div className="form-group">
        <label>Exam Type</label>
        <select name="examType" onChange={handleChange}>
          <option value="">Select Exam Type</option>
          <option value="CONTINUOUS INTERNAL 1">CIE 1</option>
          <option value="CONTINUOUS INTERNAL 2">CIE 2</option>
          <option value="MODEL">MODEL</option>
        </select>
      </div>

      <div className="bloom-section">

        <h3>Bloom Level Per Unit</h3>

        <div className="form-group">
          <label>Unit 1</label>
          <input type="number" onChange={(e)=>handleBloomChange(1,e.target.value)}/>
        </div>

        <div className="form-group">
          <label>Unit 2</label>
          <input type="number" onChange={(e)=>handleBloomChange(2,e.target.value)}/>
        </div>

        <div className="form-group">
          <label>Unit 3</label>
          <input type="number" onChange={(e)=>handleBloomChange(3,e.target.value)}/>
        </div>

        <div className="form-group">
          <label>Unit 4</label>
          <input type="number" onChange={(e)=>handleBloomChange(4,e.target.value)}/>
        </div>

        <div className="form-group">
          <label>Unit 5</label>
          <input type="number" onChange={(e)=>handleBloomChange(5,e.target.value)}/>
        </div>

      </div>

      <button className="generate-btn" onClick={generate}>
        Generate PDF
      </button>

        </div> {/* card */}
      </div> {/* page-wrapper */}
    </>
  );
}

export default GeneratePaperPage;