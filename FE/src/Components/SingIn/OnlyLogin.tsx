import React, { useEffect } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';
import { queryClient } from '../../router';

export default function OnlyLogin() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);

    const navigateToHome = () => {
      navigate('/');
    };

    const navigateToSignup = (data: object) => {
      navigate('/sign-up', { state: { data } });
    };

    if (urlParams.has('code')) {
      const code = urlParams.get('code');

      axios({
        method: 'get',
        url: `${import.meta.env.VITE_REACT_APP_API}/member/login`,
        params: {
          code,
        },
      }).then((res) => {
        // accesstoken => queryclient
        console.log(res)
        queryClient.setQueryData(['accessToken'], res.data?.token.Authorization);

        // refresh token => localstorage
        const { refreshToken } = res.data.token;
        localStorage.setItem('refreshToken', refreshToken);

        const isRegist = res.data.isRegistered;
        if (isRegist) {
          navigateToHome();
        } else {
          const data = {
            name: res.data.name,
            email: res.data.email,
            gender: res.data.genderType,
          };
          navigateToSignup(data);
        }
        navigateToHome();
      });
    }
  }, [location, navigate]);
  return <div>{/*  */}</div>;
}
