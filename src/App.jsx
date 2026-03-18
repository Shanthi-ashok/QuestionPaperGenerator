import { BrowserRouter, Routes, Route } from "react-router-dom";

import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";
import UploadExcelPage from "./pages/UploadExcelPage";
import GeneratePaperPage from "./pages/GeneratePaperPage";
import "./styles/global.css";

function App() {

  return (

    <BrowserRouter>

      <Routes>

        <Route path="/" element={<LoginPage />} />

        <Route path="/dashboard" element={<Dashboard />} />

        <Route path="/upload" element={<UploadExcelPage />} />

        <Route path="/generate" element={<GeneratePaperPage />} />

      </Routes>

    </BrowserRouter>

  );
}

export default App;