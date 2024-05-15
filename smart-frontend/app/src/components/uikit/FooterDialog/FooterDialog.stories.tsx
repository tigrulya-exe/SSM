import type { Meta, StoryObj } from '@storybook/react';
import Button from '@uikit/Button/Button';
import React, { useState } from 'react';
import type { FooterDialogProps } from './FooterDialog';
import FooterDialog from './FooterDialog';

type Story = StoryObj<typeof FooterDialog>;
export default {
  title: 'uikit/FooterDialog',
  component: FooterDialog,
  argTypes: {
    onOpenChange: {
      table: {
        disable: true,
      },
    },
  },
} as Meta<typeof FooterDialog>;

const FooterDialogExample: React.FC<FooterDialogProps> = (props) => {
  const [isShown, setIsShown] = useState(false);

  const handleAction = () => {
    props.onAction?.();
    setIsShown(false);
  };

  const handleOpen = () => {
    setIsShown(true);
  };

  return (
    <>
      <Button onClick={handleOpen}>Click to open first footer dialog</Button>
      <div style={{ width: '300px', height: '100px', marginLeft: '100px' }}>
        <FooterDialog {...props} isShown={isShown} onOpenChange={setIsShown} onAction={handleAction}>
          <div>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam commodo dui vel turpis mollis dignissim.
            Aliquam semper risus sollicitudin, consectetur risus aliquam, fringilla neque. Sed cursus elit eu sem
            bibendum euismod sit amet in erat. Sed id congue libero. Maecenas in commodo nisl, et eleifend lacus. Ut
            convallis eros eget justo sollicitudin pulvinar. Sed eu tellus quis erat auctor tincidunt sit amet eu augue.
            In fermentum egestas mauris vitae porttitor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            facilisi. Sed odio nunc, feugiat vel finibus dapibus, molestie a ipsum. Aenean scelerisque eget ipsum eget
            luctus.
          </div>
        </FooterDialog>
      </div>
    </>
  );
};
export const Dialog: Story = {
  args: {
    title: 'Lorem ipsum',
  },
  render: (args) => {
    return <FooterDialogExample {...args} />;
  },
};
