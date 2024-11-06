export const prepareText = (value: string, limit: number) => {
  return value.length > limit ? `${value.substring(0, limit)}...` : value;
};
