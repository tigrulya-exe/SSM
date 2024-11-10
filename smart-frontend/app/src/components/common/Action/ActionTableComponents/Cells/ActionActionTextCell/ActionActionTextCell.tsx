import React from 'react';
import TableCell from '@uikit/Table/TableCell/TableCell';
import type { AdhAction } from '@models/adh';
import CellBigTextWrapper from '@uikit/Table/TableCell/AdvancedCells/CellSubComponents/CellBigTextWrapper/CellBigTextWrapper';
import { Link } from 'react-router-dom';

interface ActionActionTextCellProps {
  action: AdhAction;
}

const ActionActionTextCell = ({ action: { textRepresentation, id } }: ActionActionTextCellProps) => {
  return (
    <TableCell>
      <Link to={`/actions/${id}`} className="text-link">
        <CellBigTextWrapper text={textRepresentation} />
      </Link>
    </TableCell>
  );
};

export default ActionActionTextCell;
