import router from "@/router"

const handlePage = (emailCut) => {
    router.push(`/${emailCut}`)
  }
  export{handlePage}