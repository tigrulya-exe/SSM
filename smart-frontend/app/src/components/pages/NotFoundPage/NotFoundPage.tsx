import React from 'react';
import { Link } from 'react-router-dom';
import s from './NotFoundPage.module.scss';
import ErrorTextContainer from '@commonComponents/ErrorPageContent/ErrorTextContainer/ErrorTextContainer';
import ErrorPageContent from '@commonComponents/ErrorPageContent/ErrorPageContent';

const NotFoundPage = () => {
  return (
    <div className={s.notFoundPage}>
      <ErrorPageContent errorCode="404">
        <ErrorTextContainer errorHeader="Page not found">
          <div>Page you’re trying to reach doesn’t exist or was removed</div>
          <div className={s.notFoundPage__link}>
            Please return to{' '}
            <Link className="text-link" to="/">
              Dashboard
            </Link>{' '}
            page
          </div>
        </ErrorTextContainer>
      </ErrorPageContent>
    </div>
  );
};

export default NotFoundPage;
