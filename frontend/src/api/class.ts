import request from '../utils/request'

export interface ClassForm {
    id?: number
    className: string
    grade: string
    headTeacherId?: number | null
    semester?: string
    status?: number
}

export const getClassList = (params?: { keyword?: string; status?: number }) =>
    request.get('/api/classes', { params })

export const addClass = (data: ClassForm) =>
    request.post('/api/classes', data)

export const updateClass = (id: number, data: ClassForm) =>
    request.put(`/api/classes/${id}`, data)

export const updateClassStatus = (id: number, status: number) =>
    request.put(`/api/classes/${id}/status`, { status })

export const deleteClass = (id: number) =>
    request.delete(`/api/classes/${id}`)

export const getTeachers = () =>
    request.get('/api/classes/teachers')

export interface StudentForm {
    id?: number
    name: string
    gender?: number
    birthday?: string
    classId?: number | null
    remark?: string
    status?: number
}

export const getStudentList = (params: {
    pageNum?: number; pageSize?: number
    classId?: number | null; keyword?: string; status?: number
}) => request.get('/api/students', { params })

export const getStudentDetail = (id: number) =>
    request.get(`/api/students/${id}`)

export const addStudent    = (data: StudentForm)          => request.post('/api/students', data)
export const updateStudent = (id: number, data: StudentForm) => request.put(`/api/students/${id}`, data)
export const deleteStudent = (id: number)                 => request.delete(`/api/students/${id}`)
export const changeClass   = (id: number, classId: number) =>
    request.put(`/api/students/${id}/change-class`, { classId })

export interface ParentBindForm {
    studentId: number; parentId: number; relation: string
}
export const bindParent   = (data: ParentBindForm)  => request.post('/api/students/bind-parent', data)
export const unbindParent = (bindId: number)         => request.delete(`/api/students/unbind-parent/${bindId}`)
export const auditBind    = (bindId: number, status: number, auditorId: number) =>
    request.put(`/api/students/audit-bind/${bindId}`, { status, auditorId })

export const getParentUsers = (keyword?: string) =>
    request.get('/api/users', { params: { role: 'parent', keyword, pageSize: 50 } })