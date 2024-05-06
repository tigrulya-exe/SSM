import React from 'react';
import cn from 'classnames';
import s from './Tabs.module.scss';

export interface TabsBlockProps extends React.HTMLAttributes<HTMLDivElement> {
  dataTest?: string;
}

const TabsBlock: React.FC<TabsBlockProps> = ({ children, className, dataTest = 'tab-container', ...props }) => {
  const classes = cn(className, s.tabsBlock, s['tabsBlock_primary']);

  return (
    <div className={classes} {...props} data-test={dataTest}>
      {children}
    </div>
  );
};
export default TabsBlock;
