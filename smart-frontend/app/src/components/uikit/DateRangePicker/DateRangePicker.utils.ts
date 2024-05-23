import { format } from '@utils/date';

export const formatDate = (date?: Date) => (date ? format(date, 'dd/MM/yyyy HH:mm:ss') : '');
