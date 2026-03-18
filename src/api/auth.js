import API from "./axios";

export const loginUser = (data) => {
  return API.post("/auth/login", data);
};