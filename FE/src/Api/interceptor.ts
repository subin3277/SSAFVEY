/* eslint-disable no-param-reassign */
import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { getRefresh } from './member';
import { queryClient } from '../router';

const axiosInstance: AxiosInstance = axios.create({
  baseURL: 'https://k8a608.p.ssafy.io/api',
  // baseURL: "https://localhost:8080/api",
});

axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // You can modify the request config here
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    // You can modify the response here
    return response;
  },
  async (error) => {
    const {
      config,
      response: { status },
    } = error;
    // axiosInstance.interceptors.response.eject(inter);
    if (status === 401) {
      console.log('Error!!!');
      const originRequest = config;
      const token = await getRefresh(localStorage.getItem('refreshToken'));
      const accessToken = token.Authorization;
      queryClient.setQueryData(['accessToken'], accessToken);
      localStorage.setItem('refreshToken', token?.refreshToken);

      axios.defaults.headers.common.Authorization = `Bearer ${queryClient.getQueryData(['accessToken'])}`;
      originRequest.headers.Authorization = `Bearer ${queryClient.getQueryData(['accessToken'])}`;
      return axios(originRequest);
    }
    return Promise.reject(error);
  },
);

export default axiosInstance;