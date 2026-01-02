import { Car } from "./Car"
import { UserRole } from "./UserRole"

export interface Profile {
    username: string
    role: UserRole
    firstname: string
    lastname: string
    email: string
    birthdate: Date | string
    profilePictureBase64: string | null;
    carType: Car | null
    rating: number
    totalTrips: number
    balance: number
    drivenDistance: number
    
}