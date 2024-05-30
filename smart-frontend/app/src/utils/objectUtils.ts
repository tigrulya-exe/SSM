export const getValueByPath = (object: unknown, path: string) => {
  const value = path.split('.').reduce((acc, c) => acc && acc[c as keyof typeof object], object);
  return value;
};

export const isObject = (obj: unknown) => Object.prototype.toString.call(obj) === '[object Object]';

type Scalar = number | string | boolean;
type Structure = object | object[] | Scalar;

export const structureTraversal = (structure: Structure, callback?: (subItem: Structure) => Structure): Structure => {
  if (Array.isArray(structure)) {
    return structure.map((item) => structureTraversal(item, callback));
  }
  if (isObject(structure)) {
    return Object.entries(structure).reduce(
      (res, [key, val]) => {
        res[key] = structureTraversal(val, callback);
        return res;
      },
      {} as Record<string, Structure>,
    );
  }

  return callback ? callback(structure) : structure;
};

export const objectTrim = (object: Structure) =>
  structureTraversal(object, (item) => (typeof item === 'string' ? item.trim() : item));

export const areAllFieldsUndefined = (errors: object) => !Object.values(errors).some((v) => !!v);
