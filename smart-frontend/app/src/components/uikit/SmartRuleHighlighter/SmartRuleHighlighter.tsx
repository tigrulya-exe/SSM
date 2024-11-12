import { getTokens } from '@utils/smartRule/smartRuleTokensProvider.utils';
import s from './SmartRuleHighlighter.module.scss';
import cn from 'classnames';

export interface SmartRuleHighlighterProps {
  rule: string;
}

const SmartRuleHighlighter = ({ rule }: SmartRuleHighlighterProps) => {
  const tokens = getTokens(rule);
  const elements: React.ReactElement[] = [];

  for (let i = 0; i < tokens.length; i++) {
    const token = tokens[i];
    const nextToken = tokens[i + 1];

    let spacesCount = 0;
    let cursor = token.startIndex;
    for (; cursor < (nextToken?.startIndex || rule.length); cursor++) {
      if (rule[cursor] === ' ') {
        spacesCount += 1;
      }
    }

    const tokenText = rule.substring(token.startIndex, cursor - spacesCount);
    elements.push(
      <span key={`${i}-token`} className={cn(s.token, s[token.scopes])}>
        {tokenText}
      </span>,
    );

    if (spacesCount) {
      elements.push(
        <span key={`${i}-space`} className={cn(s.token)}>
          {'\u00A0'.repeat(spacesCount)}
        </span>,
      );
    }
  }

  return <span className={s.smartRuleHighlighter}>{elements}</span>;
};

export default SmartRuleHighlighter;
