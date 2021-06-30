type Item = {
    id: string;
    name: string;
    length: number;
    width: number;
    height: number;
    weight: number;
    quantity: number;
    cargoStyle: number;
    rotatable: boolean;
    stackable: boolean;
    selfStackable: boolean;
};

type RegularGroup = {
    id: string;
    name: string;
    items: Array<Item>;
    color: string | null;
    stackGroupOnly: boolean;
    alreadyLoaded: boolean;
};

type AliasGroup = {
    id: string;
    name: string;
    items: Array<Item>;
    color: string | null;
    stackGroupOnly: boolean;
    alreadyLoaded: boolean;
    groups: Array<RegularGroup>;
};

export type Container = {
    id: string;
    name: string;
    length: number;
    width: number;
    height: number;
    maxAllowedVolume: number;
    maxAllowedWeight: number;
    loadPlan: any | null;
};

export type Config = {
    cargoSupport: number;
    lightUnstackableWeightLimit: number;
    maxHeavierCargoOnTop: number;
    allowHeavierCargoOnTop: boolean;
    keepGroupsTogether: boolean;
};

export type Group = Array<RegularGroup> | AliasGroup;

export interface ConvertedXlsDto {
    containers: Array<Container>;
    config: Config;
    groups: Array<Group>;
}

export enum SheetOptions {
    Config = 'Config',
    Containers = 'Containers',
    Cargos = 'Cargos',
}
