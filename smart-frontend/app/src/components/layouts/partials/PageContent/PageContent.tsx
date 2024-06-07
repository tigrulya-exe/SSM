import React from 'react';
import s from './PageContent.module.scss';
import cn from 'classnames';

const PageContent: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({ className, children, ...props }) => {
  console.info(className);
  return (
    <main className={cn(className, s.pageContent)} {...props}>
      {children}
    </main>
  );
};

export default PageContent;
