/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { useEffect } from 'react';
import {
  autoUpdate,
  flip,
  FloatingFocusManager,
  FloatingPortal,
  offset,
  shift,
  useDismiss,
  useFloating,
  useInteractions,
  useRole,
} from '@floating-ui/react';
import { getWidthStyles } from '@uikit/Popover/Popover.utils';
import { useForwardRef } from '@hooks/useForwardRef';
import type { ChildWithRef } from '@uikit/types/element.types';
import type { PopoverOptions } from '@uikit/Popover/Popover.types';

export interface PopoverProps extends PopoverOptions {
  isOpen: boolean;
  onOpenChange: (isOpen: boolean) => void;
  triggerRef: React.RefObject<HTMLElement>;
  children: ChildWithRef;
  initialFocus?: number | React.MutableRefObject<HTMLElement | null>;
}
const Popover: React.FC<PopoverProps> = ({
  isOpen,
  onOpenChange,
  children,
  triggerRef,
  placement = 'bottom-start',
  offset: offsetValue = 10,
  dependencyWidth,
  initialFocus,
}) => {
  const { refs, floatingStyles, context } = useFloating({
    placement,
    open: isOpen,
    onOpenChange,
    middleware: [offset(offsetValue), flip(), shift({ padding: 8 })],
    whileElementsMounted: autoUpdate,
  });

  useEffect(() => {
    triggerRef.current && refs.setReference(triggerRef.current);
  }, [triggerRef, refs]);

  const dismiss = useDismiss(context);
  const role = useRole(context);

  const { getFloatingProps } = useInteractions([dismiss, role]);

  const popoverPanel = React.Children.only(children);
  const ref = useForwardRef(refs.setFloating, children.ref);
  const panelStyle = { ...(children.props.style ?? {}), ...floatingStyles };
  if (dependencyWidth) {
    Object.entries(getWidthStyles(dependencyWidth, triggerRef)).forEach(([cssProperty, value]) => {
      panelStyle[cssProperty] = value;
    });
  }

  return (
    <FloatingPortal>
      {isOpen && (
        <FloatingFocusManager context={context} initialFocus={initialFocus}>
          {React.cloneElement(popoverPanel, { ref, ...children.props, style: panelStyle, ...getFloatingProps() })}
        </FloatingFocusManager>
      )}
    </FloatingPortal>
  );
};
export default Popover;
