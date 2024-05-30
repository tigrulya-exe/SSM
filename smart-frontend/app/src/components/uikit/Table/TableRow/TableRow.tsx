import React from 'react';

export type TableRowProps = React.HTMLAttributes<HTMLTableRowElement>;

const TableRow = React.forwardRef<HTMLTableRowElement, TableRowProps>(
  ({ children, className, ...props }: TableRowProps, ref) => {
    return (
      <tr ref={ref} className={className} {...props}>
        {children}
      </tr>
    );
  },
);

export default TableRow;
