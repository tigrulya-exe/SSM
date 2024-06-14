import React from 'react';
import type { TextProps } from '@uikit/Text/Text';
import Text from '@uikit/Text/Text';
import cn from 'classnames';
import s from './Title.module.scss';

const Title: React.FC<TextProps> = ({ className, children, ...props }) => {
  return (
    <Text className={cn(className, s.title)} {...props}>
      {children}
    </Text>
  );
};
export default Title;
