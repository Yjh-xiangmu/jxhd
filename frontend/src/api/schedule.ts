import request from '../utils/request'

// ── 课表 ──────────────────────────────────────────
export interface ScheduleForm {
  id?: number
  classId: number
  dayOfWeek: number
  periodNo: number
  subject: string
  teacherId?: number | null
  startTime?: string
  endTime?: string
}

export const getSchedule = (classId: number) =>
  request.get('/api/schedules', { params: { classId } })

export const saveScheduleEntry = (data: ScheduleForm) =>
  request.post('/api/schedules', data)

export const deleteScheduleEntry = (id: number) =>
  request.delete(`/api/schedules/${id}`)

export const getMyClass = () =>
  request.get('/api/schedules/my-class')

// ── 考勤 ──────────────────────────────────────────
export interface AttendanceForm {
  studentId: number
  classId: number
  date: string
  status: number
  absenceReason?: string
  contactMethod?: string
  contactNote?: string
}

export const getAttendance = (classId: number, date: string) =>
  request.get('/api/attendance', { params: { classId, date } })

export const recordAttendance = (data: AttendanceForm) =>
  request.post('/api/attendance', data)

export const batchPresent = (classId: number, date: string, studentIds: number[]) =>
  request.post('/api/attendance/batch-present', { classId, date, studentIds })
