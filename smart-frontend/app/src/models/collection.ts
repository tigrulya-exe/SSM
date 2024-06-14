export interface Collection<T> {
  items: T[];
}
export interface PaginateCollection<T> extends Collection<T> {
  total: number;
}
