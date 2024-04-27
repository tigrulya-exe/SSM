export type FieldSize = 'medium' | 'small';

export interface FieldProps {
  size?: FieldSize;
  disabled?: boolean;
  hasError?: boolean;
}
