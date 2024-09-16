const formatDate = (createdAt: string) => {
    const now = Date.now()
    const tweetDate = new Date(createdAt).getTime() // 假设 createdAt 是 ISO 8601 字符串
    const diffInSeconds = Math.floor((now - tweetDate) / 1000)
    const diffInMinutes = Math.floor(diffInSeconds / 60)
    const diffInHours = Math.floor(diffInMinutes / 60)
    if (diffInSeconds < 60) {
      return `${diffInSeconds}秒前`
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes}分钟前`
    } else if (diffInHours < 24) {
      return `${diffInHours}小时前`
    } else {
      return formatDateToPattern(createdAt)
    }
  }
  const formatDateToPattern = (date: string) => {
    const dateObj = new Date(date);
    const now = new Date(); // 获取当前时间
    const yearStr = dateObj.getFullYear();
    const monthStr = (dateObj.getMonth() + 1).toString() // 为了保证月份是两位数，如'02'表示二月
    const dayStr = dateObj.getDate().toString() // 为了保证日期是两位数，如'09'表示九号
    const isCurrentYear = yearStr === now.getFullYear();
    const formattedDate = isCurrentYear
      ? `${monthStr}月${dayStr}日`
      : `${yearStr}年${monthStr}月${dayStr}日`;
  
    return formattedDate;
  }
  export {formatDate}