import type React from 'react';

export type AlignType = 'left' | 'center' | 'right';

export type TableFilterRenderer = (closeCallback: () => void, isOpen: boolean) => React.ReactNode;

export type TableCellValueSchema =
  | {
      type: SchemaColumnType;
      cellRenderer?: never;
    }
  | {
      type?: never;
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      cellRenderer: (model: any) => React.JSX.Element;
    };

export enum SchemaColumnType {
  DateTime = 'DATE_TIME',
  Duration = 'DURATION',
  Memory = 'MEMORY',
  Text = 'TEXT',
  BigText = 'BIG_TEXT',
  StatusText = 'STATUS_TEXT',
  Percent = 'PERCENT',
}

export type TableColumnSchema = {
  name: string;
  label?: React.ReactNode;
  headerAlign?: AlignType;
  align?: AlignType;
  required?: boolean;
  schema?: TableCellValueSchema;
  isSortable?: boolean;
  width?: string;
  minWidth?: string;
  filterRenderer?: TableFilterRenderer;
  filterName?: string;
  subColumns?: TableColumnSchema[];
};
