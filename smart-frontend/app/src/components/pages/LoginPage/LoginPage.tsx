import React from 'react';
import { useStore } from '@hooks';
import { Navigate, useLocation } from 'react-router-dom';
import { AUTH_STATE } from '@store/authSlice';
import NonAuthLayout from '@layouts/NonAuthLayout/NonAuthLayout';
import LoginForm from '@pages/LoginPage/LoginForm/LoginForm';

type RedirectLocationState = {
  from: string;
};

const LoginPage: React.FC = () => {
  const { authState } = useStore((s) => s.auth);

  const location = useLocation();
  const from = (location.state as RedirectLocationState)?.from || '/';

  if (authState === AUTH_STATE.Authed) {
    return <Navigate to={from} replace />;
  }

  return (
    <NonAuthLayout>
      <LoginForm />
    </NonAuthLayout>
  );
};

export default LoginPage;
