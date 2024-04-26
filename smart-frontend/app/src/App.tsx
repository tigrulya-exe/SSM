import React from 'react';
import './styles/app.scss';
import { Button, IconButton, Input } from '@uikit';

function App() {
  return (
    <>
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
    </>
  );
}

export default App;
