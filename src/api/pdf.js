import API from "./axios";

export const generatePDF = (data) => {

  return API.post("/pdf/generate-pdf", data, {
    responseType: "blob"
  });

};