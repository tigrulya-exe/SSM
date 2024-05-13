import React, { useEffect, useRef, useState } from 'react';
import cn from 'classnames';

import Popover from '@uikit/Popover/Popover';
import FormFieldErrorPanel from './FormFieldErrorPanel/FormFieldErrorPanel';
import FormFieldHint from './FormFieldHint/FormFieldHint';

import s from './FormField.module.scss';
import InputAssistance from '@uikit/InputAssistance/InputAssistance';
import type { LabeledFieldProps } from '@uikit/LabeledField/LabeledField';
import LabeledField from '@uikit/LabeledField/LabeledField';

interface FormFieldProps extends LabeledFieldProps {
  children: React.ReactElement<{ hasError?: boolean }>;
  hasError?: boolean;
  error?: string;
  hintId?: string;
  interactiveComponentRef?: React.MutableRefObject<HTMLElement>;
}

const FormField: React.FC<FormFieldProps> = ({
  hasError: hasForceError,
  error,
  hint,
  hintId,
  children,
  className,
  interactiveComponentRef,
  onFocus,
  onBlur,
  ...props
}) => {
  const isErrorField = !!error || hasForceError;

  const classes = cn(className, s.formField, {
    'has-error': isErrorField,
  });

  const formFieldRef = useRef(null);
  const [isAssistanceOpen, setIsAssistanceOpen] = useState(false);
  const [isErrorOpen, setIsErrorOpen] = useState(false);

  const isRealAssistanceOpen = isAssistanceOpen && !error;
  const isRealErrorOpen = isErrorOpen && !!error;

  useEffect(() => {
    if (hint && !hintId) {
      console.error('hintId is undefined');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleInputFocus = (e: React.FocusEvent<HTMLInputElement>) => {
    setIsAssistanceOpen(true);
    setIsErrorOpen(true);
    onFocus?.(e);
  };

  const handleInputBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    // We can catch Blur when click by Popover. Focus of field is loose, but this must not hide field's popover
    if (!isRealErrorOpen) {
      setIsErrorOpen(false);
    }
    onBlur?.(e);
  };

  return (
    <>
      <LabeledField
        //
        {...props}
        className={classes}
        hint={<FormFieldHint description={hint} hasError={isErrorField} />}
        ref={formFieldRef}
        onFocus={handleInputFocus}
        onBlur={handleInputBlur}
      >
        {React.Children.only(
          React.cloneElement(children, {
            ...children.props,
            hasError: isErrorField,
          }),
        )}
        <Popover
          isOpen={isRealErrorOpen}
          onOpenChange={setIsErrorOpen}
          triggerRef={formFieldRef}
          dependencyWidth="min-parent"
          offset={4}
          initialFocus={formFieldRef}
        >
          <FormFieldErrorPanel>{error}</FormFieldErrorPanel>
        </Popover>
      </LabeledField>
      {hint && (
        <InputAssistance
          id={hintId || 'unknown'}
          triggerRef={formFieldRef}
          focusRef={interactiveComponentRef}
          isOpen={isRealAssistanceOpen}
          onOpenChange={setIsAssistanceOpen}
        >
          {hint}
        </InputAssistance>
      )}
    </>
  );
};

export default FormField;
