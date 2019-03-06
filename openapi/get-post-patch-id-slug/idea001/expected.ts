// Expected interfaces created from this OpenAPI Specification
// https://gist.github.com/Adeynack/c42b928189eb4a227a4f42a10b6059b5

type UUID = string // for the example

export interface PersonPatch {
    firstName?: string
    lastName?: string
    birthdate?: Date
    description?: string
}

export interface PersonReq {
    firstName: string           // required
    lastName: string            // required 
    birthdate: Date             // required
    description?: string        // optional
}

export interface PersonCreate {
    // optional slug
    slug?: string
    // everything from `PersonReq`, with some required.
    firstName: string
    lastName: string
    birthdate: Date
    description?: string
}

export interface Person {
    // required ID
    id: UUID
    // required slug
    slug: string
    // everything from `PersonReq`, with some required.
    firstName: string
    lastName: string
    birthdate: Date
    description?: string
}
