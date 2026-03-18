import API from "./axios";

export const uploadExcel = (file) => {

  const formData = new FormData();
  formData.append("file", file);

  return API.post("/questions/upload", formData);

};