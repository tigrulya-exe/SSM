export const prepareLabel = (value: string, limit: number) => {
  if (value === '') return '-';

  return value.length > limit ? `${value.slice(0, limit / 2)} <...> ${value.slice(-limit / 2)}` : value;
};
