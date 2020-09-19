import { BridgeContext } from "doric";

export type PathLike = string;

export type Stats = {};

export function fs(context: BridgeContext) {
  return {
    getDocumentsPath: (options?: { external?: boolean }) => {
      return context.callNative("fs", "getDocumentsPath", options) as Promise<
        PathLike
      >;
    },

    exists: (path: PathLike) => {
      return context.callNative("fs", "exists", path) as Promise<boolean>;
    },

    stat: (path: PathLike) => {
      return context.callNative("fs", "stat", path) as Promise<Stats>;
    },

    isFile: (path: PathLike) => {
      return context.callNative("fs", "isFile", path) as Promise<boolean>;
    },

    isDirectory: (path: PathLike) => {
      return context.callNative("fs", "isDirectory", path) as Promise<boolean>;
    },

    mkdir: (path: PathLike) => {
      return context.callNative("fs", "mkdir", path) as Promise<string>;
    },

    readdir: (path: PathLike) => {
      return context.callNative("fs", "readdir", path) as Promise<string[]>;
    },

    readFile: (path: PathLike) => {
      return context.callNative("fs", "readFile", path) as Promise<string>;
    },

    writeFile: (path: PathLike, content: string) => {
      return context.callNative("fs", "writeFile", {
        path,
        content,
      }) as Promise<number>;
    },

    appendFile: (path: PathLike, content: string) => {
      return context.callNative("fs", "appendFile", {
        path,
        content,
      }) as Promise<number>;
    },

    delete: (path: PathLike) => {
      return context.callNative("fs", "delete", path) as Promise<boolean>;
    },
  };
}
