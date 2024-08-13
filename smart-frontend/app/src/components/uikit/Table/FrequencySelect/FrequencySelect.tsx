import React from 'react';

import s from './FrequencySelect.module.scss';
import Select from '@uikit/Select/SingleSelect/Select/Select';
import type { SelectOption } from '@uikit/Select/Select.types';
export interface FrequencySelectProps {
  value: number;
  options?: SelectOption<number>[];
  onChange: (frequency: number) => void;
}

const defaultOptions: SelectOption<number>[] = [
  { label: '1 sec', value: 1 },
  { label: '2 sec', value: 2 },
  { label: '5 sec', value: 5 },
  { label: '10 sec', value: 10 },
];

const FrequencySelect = ({ options = defaultOptions, onChange, value }: FrequencySelectProps) => {
  const handleChange = (frequency: number | null) => {
    if (frequency) {
      onChange(frequency);
    }
  };

  return (
    <Select
      //
      className={s.frequencySelect}
      options={options}
      value={value}
      onChange={handleChange}
    />
  );
};

export default FrequencySelect;
