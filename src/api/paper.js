import API from "./axios";

export const generatePaper = (data) => {

  return API.post("/papers/generate", data);

};