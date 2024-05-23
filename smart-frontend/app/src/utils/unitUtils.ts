export type ValueSegment = {
  value: number;
  unit: string;
};

export const parseValueSegments = (s: string, delimiter = ' '): ValueSegment[] => {
  const parts = s.split(delimiter);
  const valueSegments = parts.filter((s) => s).map((p) => parseValueSegment(p));

  return valueSegments;
};

const parseValueSegment = (valueSegment: string): ValueSegment => {
  const valueMatch = valueSegment.match(/^-?\d+/g);
  const valuePart = valueMatch !== null ? valueMatch[0] : '';
  let unitPart = '';

  const unitMatch = valueSegment.split(valuePart);
  if (unitMatch.length === 2 && unitMatch[1] !== '') {
    unitPart = unitMatch[1];
  }

  return { value: Number(valuePart), unit: unitPart };
};

export const stringifyValueSegments = (valueSegments: ValueSegment[]): string => {
  const result = valueSegments.map(({ value, unit }) => `${value}${unit}`).join(' ');
  return result;
};
