import React, { useMemo } from 'react';
import { TableMultiSelectFilter } from '@uikit/Table/TableFilter';
import type { AdhActionsFilter } from '@models/adh';
import { useStore } from '@hooks';

const ActionsHostsFilter: React.FC = () => {
  const hosts = useStore(({ adh }) => adh.actionsTable.relatedData.hosts);
  const hostsOption = useMemo(() => {
    return hosts.map((host) => ({
      value: host.id,
      label: host.host,
    }));
  }, [hosts]);

  return <TableMultiSelectFilter<AdhActionsFilter, string> filterName="hosts" options={hostsOption} />;
};

export default ActionsHostsFilter;
