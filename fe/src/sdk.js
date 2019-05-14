import axios from './utils/axiosConfig'

export const getUser = () => axios.get("/getUser")
    .then( res => res)
    .catch();