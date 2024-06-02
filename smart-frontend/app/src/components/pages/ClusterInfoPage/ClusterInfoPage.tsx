import React from 'react';
import { Button, IconButton, Input } from '@uikit';

const ClusterInfoPage: React.FC = () => {
  return (
    <div>
      <span>Cluster Info</span>
      <div>
        <div>
          Button: <br /> <br />
          <Button>Some label</Button>
        </div>
        <br />
        <br />
        <div>
          IconButton: <br /> <br />
          <IconButton icon="chevron" />
        </div>
        <br />
        <br />
        <div>
          Input: <br /> <br />
          <Input endAdornment={<IconButton icon="search" variant="secondary" size={16} />} />
        </div>
      </div>
    </div>
  );
};

export default ClusterInfoPage;
