// declare module 'fx' {
//   export const curry: any;
//   export function reduce<T, R>(f: (acc: R, value: T) => R, acc: R, iter?: Iterable<T>): R;
//   export function go(...args: any[]): any;
//   export function take<T>(l: number, iter: Iterable<T>): T[];
//   export function slice<T>(l: number, iter: Iterable<T>): T[];
//   export function map<T>(l: number, iter: Iterable<T>): T[];
// }

import './fx.js';

declare function curry(f);
declare function reduce(f, acc, iter?);
declare function go(...args);
declare function take(l, iter?);
declare function slice(l, iter?);
declare function map(f, iter?);
