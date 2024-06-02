import React from 'react';
import { Link } from 'react-router-dom';
import s from './Breadcrumbs.module.scss';
import type { BreadcrumbsItemConfig } from '@routes/routes.types';
import { useBreadcrumbs } from '@hooks';

const Breadcrumbs: React.FC = () => {
  const breadcrumbsList = useBreadcrumbs();

  return (
    <ul className={s.breadcrumbs}>
      {breadcrumbsList.map(({ label, href }) => (
        <CrumbsItem label={label} href={href} key={label + href} />
      ))}
    </ul>
  );
};

export default Breadcrumbs;

const CrumbsItem: React.FC<BreadcrumbsItemConfig> = ({ label, href }) => {
  return <li>{href ? <Link to={href}>{label}</Link> : <span>{label}</span>}</li>;
};
