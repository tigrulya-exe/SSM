import React from 'react';
import s from '../FormField.module.scss';
import Tooltip from '@uikit/Tooltip/Tooltip';
import cn from 'classnames';
import { Icon } from '@uikit';

interface FormFieldHintProps {
  description?: React.ReactNode;
  hasError?: boolean;
}
const FormFieldHint: React.FC<FormFieldHintProps> = ({ description, hasError }) => {
  return (
    <>
      {hasError && <Icon name="status-error" className={s.formField__marker} size={16} />}
      {description && (
        <Tooltip label={description} placement="top-start">
          <Icon name="status-info" className={cn(s.formField__marker, s.formField__marker_info)} size={16} />
        </Tooltip>
      )}
    </>
  );
};

export default FormFieldHint;
