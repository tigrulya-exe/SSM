import Button from '@uikit/Button/Button';
import ts from './Tabs.module.scss';

export interface TabActionsProps {
  isApplyDisable?: boolean;
  onApply: () => void;
  onRevert: () => void;
}

const TabActions = ({ isApplyDisable = false, onApply, onRevert }: TabActionsProps) => (
  <div className={ts.dateRangePickerTab__rightFooterButtons}>
    <Button size="small" variant="secondary" onClick={onRevert}>
      Revert
    </Button>
    <Button disabled={isApplyDisable} size="small" onClick={onApply}>
      Apply
    </Button>
  </div>
);

export default TabActions;
