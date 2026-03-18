import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import "../styles/Dashboard.css";

function Dashboard() {

  return (
    <>
      <Navbar />
      <div className="page-wrapper">
        <div className="card">
          <h2 className="page-title">Question Paper Generator</h2>

          <div className="dashboard-buttons">
            <Link to="/upload">
              <button className="btn">📁 Upload Excel</button>
            </Link>

            <Link to="/generate">
              <button className="btn">🖨️ Generate Paper</button>
            </Link>
          </div>
        </div>
      </div>
    </>
  );

}

export default Dashboard;