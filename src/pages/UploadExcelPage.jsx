import React, { useState } from "react";
import { uploadExcel } from "../api/question";
import "../styles/UploadExcelPage.css";
import Navbar from "../components/Navbar";

function UploadExcelPage() {

  const [file, setFile] = useState(null);

  const handleUpload = async () => {

    try {

      const res = await uploadExcel(file);

      alert("Upload Success");

      console.log(res.data);

    } catch (err) {

      alert("Upload Failed");

    }

  };

  return (
    <>
      <Navbar />
      <div className="page-wrapper">
        <div className="card">
          <h2 className="upload-title page-title">Upload Question Excel</h2>

          <div className="upload-box">
            <input className="file-input" type="file" onChange={(e) => setFile(e.target.files[0])} />
          </div>

          <button className="btn" onClick={handleUpload}>
            Upload
          </button>
        </div>
      </div>
    </>
  );

}

export default UploadExcelPage;